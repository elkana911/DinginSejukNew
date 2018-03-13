package com.elkana.dslibrary.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.elkana.dslibrary.exception.OrderAlreadyFinished;
import com.elkana.dslibrary.exception.OrderExpired;
import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerGetBasicInfo;
import com.elkana.dslibrary.listener.ListenerGetOrder;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.mitra.NotifyNewOrderItem;
import com.elkana.dslibrary.pojo.mitra.TechnicianReg;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;
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
import com.google.firebase.database.ServerValue;
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

    public static final String REF_ORDERS_CUSTOMER_AC_PENDING = "orders/ac/pending/customer";
    public static final String REF_ORDERS_MITRA_AC_PENDING = "orders/ac/pending/mitra";
    public static final String REF_ORDERS_CUSTOMER_AC_FINISHED = "orders/ac/finished/customer";
    public static final String REF_ORDERS_MITRA_AC_FINISHED = "orders/ac/finished/mitra";

    public static final String REF_ASSIGNMENTS_PENDING = "assignments/ac/pending";
    public static final String REF_ASSIGNMENTS_FIGHT = "assignments/ac/fight";
    public static final String REF_MITRA_AC = "mitra/ac";
    public static final String REF_TECHNICIAN_AC = "technicians/ac";


    public static void deletePath(DatabaseReference path, final ListenerModifyData listener) {
        path.setValue(null, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (listener == null)
                    return;

                if (databaseError != null)
                    listener.onError(databaseError.toException());
                else
                    listener.onSuccess();
            }
        });
    }

    public static void movePath(final DatabaseReference fromPath, final DatabaseReference toPath, final boolean copyOnly, final ListenerModifyData listener) {
        //copy first then delete
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
//                            System.out.println("Copy failed");
                        } else {
//                            System.out.println("Success");
                            // DELETE fromPath HERE
                            if (!copyOnly)
                                deletePath(fromPath, listener);

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (listener != null)
                    listener.onError(databaseError.toException());

            }
        });
    }

    //untested per 12 mar 18
    public static void moveOrderFromPendingToFinished(String customerId, String mitraId, String orderId, ListenerModifyData listener) {
        movePath(Orders_getPendingCustomerRef(customerId, orderId)
                , FirebaseDatabase.getInstance().getReference(REF_ORDERS_CUSTOMER_AC_FINISHED).child(customerId).child(orderId)
                , true
                , listener);

    }

    public static DatabaseReference Assignment_fight(String orderId) {
        return FirebaseDatabase.getInstance().getReference(REF_ASSIGNMENTS_FIGHT)
                .child(orderId)
                ;
    }

    public static void Assignment_deleteFight(String orderId, final ListenerModifyData listener) {
        Assignment_fight(orderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener == null)
                    return;

                if (task.isSuccessful()) {
                    listener.onSuccess();
                } else {
                    listener.onError(task.getException());
                }
            }
        });
    }

    public static DatabaseReference Assignment_getPendingRef(String technicianId, String assignmentId) {
        return FirebaseDatabase.getInstance().getReference(REF_ASSIGNMENTS_PENDING)
                .child(technicianId)
                .child(assignmentId);
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

    }

    public static DatabaseReference Assignment_getServiceItemsRef(String techId, String assignmentId) {
        return Assignment_getPendingRef(techId, assignmentId)
                .child("svcItems");
    }

    public static void Order_SetStatus(final String mitraId, final String customerId, final String orderId, final String assignmentId, EOrderDetailStatus newStatus, String updatedBy, final ListenerModifyData listener) {
        final Map<String, Object> keyValOrder = new HashMap<>();
//        keyValOrder.put("statusDetailId", newStatus.name());
//        keyValOrder.put("updatedTimestamp", new Date().getTime());

        long time = new Date().getTime();

        // fyi ada 3 node yg hrs diupdate:
//1.        orders/ac/pending/customer/<customerId>/<orderId>/statusDetailId
//2.        assignments/ac/pending/<technicianId>/<assignmentId>/assign/statusDetailId
//3.        orders/ac/pending/mitra/<mitraId>/<orderId>/statusDetailId
        String root = REF_ORDERS_CUSTOMER_AC_PENDING + "/" + customerId + "/" + orderId;
        keyValOrder.put(root + "/statusDetailId", newStatus.name());
        keyValOrder.put(root + "/updatedTimestamp", time);
        keyValOrder.put(root + "/updatedBy", updatedBy);

        root = REF_ORDERS_MITRA_AC_PENDING + "/" + mitraId + "/" + orderId;
        keyValOrder.put(root + "/statusDetailId", newStatus.name());
        keyValOrder.put(root + "/updatedTimestamp", time);
        keyValOrder.put(root + "/updatedBy", updatedBy);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (assignmentId != null) {
            root = REF_ASSIGNMENTS_PENDING + "/" + currentUser.getUid() + "/" + assignmentId + "/assign";
            keyValOrder.put(root + "/statusDetailId", newStatus.name());
            keyValOrder.put(root + "/updatedTimestamp", time);
            keyValOrder.put(root + "/updatedBy", updatedBy);
        }

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

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
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

    public static DatabaseReference Orders_getPendingMitraRef(String mitraId, String orderId) {
        return FirebaseDatabase.getInstance().getReference(REF_ORDERS_MITRA_AC_PENDING)
                .child(mitraId)
                .child(orderId);

    }

    public static void Orders_getPendingMitraRef(String mitraId, String orderId, final ListenerGetOrder listener) {

        Orders_getPendingMitraRef(mitraId, orderId)
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

    public static void TechnicianReg_setNotifyNewOrder(NotifyNewOrderItem obj, final ListenerModifyData listener) {
        final Map<String, Object> keyVal = new HashMap<>();

        // see NotifyNewOrderItem
        keyVal.put("orderId", obj.getOrderId());
        keyVal.put("address", obj.getAddress());
        keyVal.put("acCount", obj.getAcCount());
        keyVal.put("customerId", obj.getCustomerId());
        keyVal.put("customerName", obj.getCustomerName());
        keyVal.put("orderTimestamp", obj.getOrderTimestamp());
        keyVal.put("mitraTimestamp", obj.getMitraTimestamp());
        keyVal.put("timestamp", ServerValue.TIMESTAMP);

        TechnicianReg_getNotifyNewOrderRef(obj.getMitraId(), obj.getTechId())
                .child(obj.getOrderId())
                .updateChildren(keyVal).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public static void TechnicianReg_DeleteNotifyNewOrder(String mitraId, String techId, String orderId, final ListenerModifyData listener) {

        TechnicianReg_getNotifyNewOrderRef(mitraId, techId)
                .child(orderId)
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (listener == null)
                            return;

                        if (databaseError == null) {
                            listener.onSuccess();
                        } else {
                            listener.onError(databaseError.toException());
                        }
                    }
                });
    }

    public static void Technician_findByEmail(String email, final ListenerGetBasicInfo listener) {
        FirebaseDatabase.getInstance().getReference(REF_TECHNICIAN_AC)
                .orderByChild("email")
                .equalTo(email)
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (listener == null)
                            return;

                        if (!dataSnapshot.exists()) {
                            listener.onError(new RuntimeException("Teknisi tidak ditemukan !"));
                            return;
                        }

                        BasicInfo basicInfo = dataSnapshot.getChildren().iterator().next()
                                .getChildren().iterator().next()
                                .getValue(BasicInfo.class);

                        List<FirebaseToken> listFBTokens = new ArrayList<>();
                        // TODO: check the nodes
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren().iterator().next().getChildren()) {
//                            FirebaseToken _obj = postSnapshot.child("firebaseToken").getValue(FirebaseToken.class);
//
//                            listFBTokens.add(_obj);
//                        }

                        listener.onFound(basicInfo, listFBTokens);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (listener != null)
                            listener.onError(databaseError.toException());
                    }
                });
    }

    public static void Mitra_registerTech(final String mitraId, final TechnicianReg techReg, final ListenerModifyData listener) {

        final Map<String, Object> keyVal = new HashMap<>();

        String root = REF_MITRA_AC + "/" + mitraId + "/technicians/" + techReg.getTechId();
        String node1 = root + "/techId";
        String node2 = root + "/joinDate";
        String node3 = root + "/name";
        String node4 = root + "/suspend";
        String node5 = root + "/orderTodayCount";
        keyVal.put(node1, techReg.getTechId());
        keyVal.put(node2, ServerValue.TIMESTAMP);
        keyVal.put(node3, techReg.getName());
        keyVal.put(node4, false);
        keyVal.put(node5, 0);

        root = REF_TECHNICIAN_AC + "/" + techReg.getTechId() + "/mitra/" + mitraId;
        String node11 = root + "/mitraId";
        String node12 = root + "/joinDate";
        keyVal.put(node11, mitraId);
        keyVal.put(node12, ServerValue.TIMESTAMP);

        FirebaseDatabase.getInstance().getReference().updateChildren(keyVal).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public static DatabaseReference TechnicianReg_getNotifyNewOrderRef(String mitraId, String techId) {
        return Mitra_GetTechnicianRef(mitraId, techId).child("notify_new_order");

    }

    public static DatabaseReference Technician_GetMitraRef(String techId) {
        return FirebaseDatabase.getInstance().getReference(REF_TECHNICIAN_AC)
                .child(techId)
                .child("mitra");
    }

}
