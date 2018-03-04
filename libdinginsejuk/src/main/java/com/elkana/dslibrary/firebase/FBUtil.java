package com.elkana.dslibrary.firebase;

import android.support.annotation.NonNull;

import com.elkana.dslibrary.exception.OrderAlreadyFinished;
import com.elkana.dslibrary.exception.OrderExpired;
import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerGetOrder;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
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

    public static void Assignment_create(final String technicianId, final String custId, final String orderId, final ListenerModifyData listener) {
        //1. get orderHeader
        Orders_getPendingCustomerRef(custId, orderId, new ListenerGetOrder() {
            @Override
            public void onGetData(OrderHeader obj) {
                // cannot assign finished status
                if (EOrderStatus.isFinished(obj)) {
                    if (listener != null)
                        listener.onError(new OrderAlreadyFinished());

                    return;
                }

//                please put this validation outside
//                if (DataUtil.isExpiredOrder(obj)) {
//                    if (listener != null)
//                        listener.onError(new OrderExpired());
//
//                    return;
//                }

                FirebaseDatabase _firebaseDatabase = FirebaseDatabase.getInstance();

                DatabaseReference refAssignment = _firebaseDatabase.getReference(REF_ASSIGNMENTS_PENDING)
                        .child(technicianId)
                        .push();

                final Assignment assignment = new Assignment();

                assignment.setUid(refAssignment.getKey());

                assignment.setTechnicianId(technicianId);
                assignment.setDateOfService(obj.getDateOfService());
                assignment.setTimeOfService(obj.getTimeOfService());
                assignment.setStatusDetailId(EOrderDetailStatus.ASSIGNED.name());
                assignment.setUpdatedTimestamp(new Date().getTime());
                assignment.setCreatedDate(new Date().getTime());
                assignment.setCustomerAddress(obj.getAddressByGoogle());
                assignment.setCustomerId(obj.getCustomerId());
                assignment.setCustomerName(obj.getCustomerName());
                assignment.setLatitude(obj.getLatitude());
                assignment.setLongitude(obj.getLongitude());
                assignment.setOrderId(obj.getUid());
                assignment.setMitraId(obj.getPartyId());
                assignment.setMitraName(obj.getPartyName());

                EOrderDetailStatus newStatus = EOrderDetailStatus.convertValue(assignment.getStatusDetailId());
                String customerId = obj.getCustomerId();

                final Map<String, Object> keyValOrder = new HashMap<>();
                /*
                    customer node
                    orders/ac/pending/customer/<customerId>/<orderId>/statusDetailId
                 */
                String _root_node = REF_ORDERS_CUSTOMER_AC_PENDING + "/" + customerId + "/" + orderId;
                keyValOrder.put(_root_node + "/technicianId", technicianId);
                keyValOrder.put(_root_node + "/statusDetailId", newStatus.name());
                keyValOrder.put(_root_node + "/updatedTimestamp", new Date().getTime());

                /*
                    mitra node
                    orders/ac/pending/mitra/<mitraId>/<orderId>/statusDetailId
                 */
                _root_node = REF_ORDERS_MITRA_AC_PENDING + "/" + obj.getPartyId() + "/" + orderId;
                keyValOrder.put(_root_node + "/statusDetailId" , newStatus.name());
                keyValOrder.put(_root_node + "/updatedTimestamp", new Date().getTime());

                /*
                    assignment node
                    assignments/ac/pending/<technicianId>/<assignmentId>/assign/statusDetailId
                 */
                _root_node = REF_ASSIGNMENTS_PENDING + "/" + technicianId + "/" + assignment.getUid();
                keyValOrder.put(_root_node + "/assign", assignment);

                _firebaseDatabase.getReference().updateChildren(keyValOrder)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (listener == null)
                                    return;

                                if (task.isSuccessful())
                                    listener.onSuccess();
                                else
                                    listener.onError(task.getException());
                            }
                        });


            }

            @Override
            public void onError(Exception e) {
                if (listener != null)
                    listener.onError(e);
            }
        });
    }


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

    public static void Orders_getPendingCustomerRef(String customerId, String orderId, final ListenerGetOrder listener) {

            Orders_getPendingCustomerRef(customerId, orderId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (listener != null)
                        listener.onError(new RuntimeException("Node not found"));

                    return;
                }

                OrderHeader orderHeader = dataSnapshot.getValue(OrderHeader.class);

                if (listener != null) {
                    listener.onGetData(orderHeader);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (listener != null)
                    listener.onError(databaseError.toException());
            }
        });

    }

}
