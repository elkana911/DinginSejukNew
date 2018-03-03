package com.elkana.dslibrary.firebase;

import android.support.annotation.NonNull;

import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric on 03-Mar-18.
 */

public class FBUtil {

    /**
     * @see #Orders_getPendingCustomerRef(String, String)
     */
    public static final String REF_ORDERS_CUSTOMER_AC_PENDING = "orders/ac/pending/customer";

    public static final String REF_ORDERS_MITRA_AC_PENDING = "orders/ac/pending/mitra";
    public static final String REF_ASSIGNMENTS_PENDING = "assignments/ac/pending";
    public static final String REF_MITRA_AC = "mitra/ac";

    public static DatabaseReference Assignment_getPendingRef(String technicianId, String assignmentId) {
        return FirebaseDatabase.getInstance().getReference(REF_ASSIGNMENTS_PENDING)
                .child(technicianId)
                .child(assignmentId);
//                .child("assign");

    }

    public static void Assignment_addServiceItems(String technicianId, String assignmentId, List<ServiceItem> list, final ListenerModifyData listener) {

        Assignment_getPendingRef(technicianId, assignmentId)
                .child("svcItems")
                .setValue(list)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (listener != null) {
                        listener.onSuccess();
                    } else {
                        if (listener != null)
                            listener.onError(task.getException());
                    }
                }
            }
        });

    };

    public static void Assignment_SetStatus(final String mitraId, String technicianId, final String assignmentId, final String customerId, final String orderId, final EOrderDetailStatus newStatus, final ListenerModifyData listener) {
        Date today = new Date();

        Map<String, Object> keyValAssignment = new HashMap<>();
        keyValAssignment.put("statusDetailId", newStatus.name());
        keyValAssignment.put("updatedTimestamp", today.getTime());

        // kalo status = OTW, add to mitra/.../technicianId/jobs, key format is yyyyMMdd
        // spy dapat diketahui 1 hari ada brp order
        if (newStatus == EOrderDetailStatus.ASSIGNED || newStatus == EOrderDetailStatus.OTW) {

            DatabaseReference _ref = Mitra_GetTechnicianRef(mitraId, technicianId);
            _ref.child("jobs")
                    .child(Util.convertDateToString(today, "yyyyMMdd"))
                    .child(String.valueOf(today.getTime()))
                    .setValue(orderId);
        }

        Assignment_getPendingRef(technicianId, assignmentId)
                .child("assign")
                .updateChildren(keyValAssignment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        // also update the main order
                        Order_SetStatus(mitraId, customerId, orderId, assignmentId, newStatus, listener);

                    }
                });

    }

    public static void Order_SetStatus(final String mitraId, final String customerId, final String orderId, final String assignmentId, EOrderDetailStatus newStatus, final ListenerModifyData listener) {
        final Map<String, Object> keyValOrder = new HashMap<>();
//        keyValOrder.put("statusDetailId", newStatus.name());
//        keyValOrder.put("updatedTimestamp", new Date().getTime());

        long time = new Date().getTime();

        // fyi ada 3 node yg hrs diupdate:
//1.        orders/ac/pending/customer/<customerId>/<orderId>/statusDetailId
//2.        assignments/ac/pending/<technicianId>/<assignmentId>/assign/statusDetailId
//3.        orders/ac/pending/mitra/<mitraId>/<orderId>/statusDetailId
        String node1 = REF_ORDERS_CUSTOMER_AC_PENDING + "/" + customerId + "/" + orderId + "/statusDetailId";
        String node2 = REF_ORDERS_CUSTOMER_AC_PENDING + "/" + customerId + "/" + orderId + "/updatedTimestamp";
        keyValOrder.put(node1, newStatus.name());
        keyValOrder.put(node2, time);

        String node3 = REF_ORDERS_MITRA_AC_PENDING + "/" + mitraId + "/" + orderId + "/statusDetailId";
        String node4 = REF_ORDERS_MITRA_AC_PENDING + "/" + mitraId + "/" + orderId + "/updatedTimestamp";
        keyValOrder.put(node3, newStatus.name());
        keyValOrder.put(node4, time);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String node5 = REF_ASSIGNMENTS_PENDING + "/" + currentUser.getUid() + "/" + assignmentId + "/assign/statusDetailId";
        String node6 = REF_ASSIGNMENTS_PENDING + "/" + currentUser.getUid() + "/" + assignmentId + "/assign/updatedTimestamp";
        keyValOrder.put(node5, newStatus.name());
        keyValOrder.put(node6, time);

        FirebaseDatabase.getInstance().getReference().updateChildren(keyValOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (listener != null)
                        listener.onSuccess();
                } else {
                    if (listener != null)
                        listener.onError(task.getException());
                }
            }
        });

    }

    public static DatabaseReference Mitra_GetTechnicianRef(String mitraId, String technicianId) {
        return FirebaseDatabase.getInstance().getReference(REF_MITRA_AC)
                .child(mitraId)
                .child("technicians")
                .child(technicianId);

    }

    public static void Mitra_getAllMitra(final ListenerGetAllData listener) {
        FirebaseDatabase.getInstance().getReference(REF_MITRA_AC)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<Mitra> list = new ArrayList<>();

                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Mitra _obj = postSnapshot.child("basicInfo").getValue(Mitra.class);
//                            Log.e(TAG, _obj.toString());

                            list.add(_obj);
                        }

                        if (listener != null)
                            listener.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (listener != null)
                            listener.onError(databaseError.toException());

                    }
                });
    }


    public static DatabaseReference Orders_getPendingCustomerRef(String customerId, String orderId) {
        return FirebaseDatabase.getInstance().getReference(REF_ORDERS_CUSTOMER_AC_PENDING)
                .child(customerId)
                .child(orderId);

    }

}
