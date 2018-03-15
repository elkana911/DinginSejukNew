package com.elkana.dslibrary.firebase;

import android.support.annotation.NonNull;

import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerGetBasicInfo;
import com.elkana.dslibrary.listener.ListenerGetOrder;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.mitra.NotifyNewOrderItem;
import com.elkana.dslibrary.pojo.mitra.TechnicianReg;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.OrderUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        movePath(Order_getPendingCustomerRef(customerId, orderId)
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

    /*
delete assignment node khususnya bagian assignmentId jika status PAID or CANCELLED or FINISHED.
fyi, di list teknisi akan terlihat kosong krn ga ada assignment lagi.
 */
    public static void Assignment_delete(String technicianId, String assignmentId, final ListenerModifyData listener) {
        FBUtil.Assignment_getPendingRef(technicianId, assignmentId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    /**
     *
     * @param mitraId
     * @param customerId
     * @param orderId
     * @param assignmentId
     * @param techId biasanya selalu ada kalau assignmentId != null
     * @param newStatus
     * @param updatedBy
     * @param listener
     */
    public static void Order_SetStatus(final String mitraId, final String customerId, final String orderId, final String assignmentId, String techId, EOrderDetailStatus newStatus, String updatedBy, final ListenerModifyData listener) {
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
        if (newStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
                || newStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT
                || newStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
                )
            keyValOrder.put(root + "/statusId", EOrderStatus.FINISHED.name());

        root = REF_ORDERS_MITRA_AC_PENDING + "/" + mitraId + "/" + orderId;
        keyValOrder.put(root + "/statusDetailId", newStatus.name());
        keyValOrder.put(root + "/updatedTimestamp", time);
        keyValOrder.put(root + "/updatedBy", updatedBy);
        if (newStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
                || newStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT
                || newStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
                )
            keyValOrder.put(root + "/statusId", EOrderStatus.FINISHED.name());

        if (assignmentId != null) {
            root = REF_ASSIGNMENTS_PENDING + "/" + techId + "/" + assignmentId + "/assign";
            keyValOrder.put(root + "/statusDetailId", newStatus.name());
            keyValOrder.put(root + "/updatedTimestamp", time);
            keyValOrder.put(root + "/updatedBy", updatedBy);
            // belum tau perlu atau tdk
//            if (newStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
//                    || newStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT
//                    || newStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
//                    )
//                keyValOrder.put(root + "/statusId", EOrderStatus.FINISHED.name());
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


    public static DatabaseReference Order_getPendingCustomerRef(String customerId, String orderId) {
        return FirebaseDatabase.getInstance().getReference(REF_ORDERS_CUSTOMER_AC_PENDING)
                .child(customerId)
                .child(orderId);

    }

    public static void Order_getPending(String customerId, final String orderId, final ListenerGetOrder listener) {

        if (listener == null)
            return;

        Order_getPendingCustomerRef(customerId, orderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                                listener.onError(new RuntimeException("Order Pending Customer not found !"));

                            return;
                        }

                        final OrderHeader orderHeader = dataSnapshot.getValue(OrderHeader.class);

                        Order_getPendingMitraRef(orderHeader.getPartyId(), orderId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                                listener.onError(new RuntimeException("Order Pending Mitra not found !"));

                                            return;
                                        }

                                        OrderBucket orderBucket = dataSnapshot.getValue(OrderBucket.class);

                                        listener.onGetData(orderHeader, orderBucket);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                            listener.onError(databaseError.toException());
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                            listener.onError(databaseError.toException());
                    }
                });

    }

    public static DatabaseReference Order_getPendingMitraRef(String mitraId, String orderId) {
        return FirebaseDatabase.getInstance().getReference(REF_ORDERS_MITRA_AC_PENDING)
                .child(mitraId)
                .child(orderId);

    }
/*
    public static void Orders_getPendingMitra(String mitraId, String orderId, final ListenerGetOrder listener) {

        Order_getPendingMitraRef(mitraId, orderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            if (listener != null)
                                listener.onError(new RuntimeException("Node not found"));

                            return;
                        }

                        OrderBucket orderBucket = dataSnapshot.getValue(OrderBucket.class);

                        if (listener != null) {
                            listener.onGetData(orderBucket);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (listener != null)
                            listener.onError(databaseError.toException());
                    }
                });

    }
*/
    public static void Orders_cancel(OrderHeader orderHeader, EOrderDetailStatus newStatus, final ListenerModifyData listener) {

        if (newStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
                || newStatus == EOrderDetailStatus.CANCELLED_BY_SERVER) {
        } else {

            if (listener != null)
                listener.onError(new RuntimeException("To cancel Order, status must either Cancel by Customer or Cancel by Server"));

            return;
        }

        final EOrderDetailStatus orderStatus = EOrderDetailStatus.convertValue(orderHeader.getStatusDetailId());

        // kalo status UNHANDLED brarti blm ada assignmentId,
        // kalo status ASSIGNED brarti udah ada assignmentId. pls check about techreg jobs
        final String assignmentId = orderStatus == EOrderDetailStatus.UNHANDLED ? null : orderHeader.getAssignmentId();
        String techId = orderHeader.getTechnicianId();

        String updatedBy = (newStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER) ? String.valueOf(Const.USER_AS_COSTUMER) : String.valueOf(Const.USER_AS_MITRA);

        Order_SetStatus(orderHeader.getPartyId(), orderHeader.getCustomerId(), orderHeader.getUid(), assignmentId, techId, newStatus, updatedBy, new ListenerModifyData() {
            @Override
            public void onSuccess() {

                if (assignmentId != null) {
                    // TODO: delete assignment ? Harusnya jangan, nti teknisinya ga merasa dihargai. jd cukup CANCELLED_BY_CUSTOMER
                }

                if (listener != null)
                    listener.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                if (listener != null)
                    listener.onError(e);

            }
        });

    }

    public static void Orders_reschedule(final OrderHeader oldOrderHeader, final OrderBucket oldOrderBucket, final Date newDate, final ListenerModifyData listener) {
        // utk saat ini hanya dipake oleh customer
        final String updatedBy = String.valueOf(Const.USER_AS_COSTUMER);

        // ambil dulu value assignmentId dan techId krn wkt reschedule akan diset null
        final String lastAssignmentId = oldOrderHeader.getAssignmentId();
        final String lastTechId = oldOrderHeader.getTechnicianId();

        OrderUtil.setRescheduleOrder(oldOrderHeader, oldOrderBucket, newDate, updatedBy);

        // just replace, no push fb logic
        Order_getPendingCustomerRef(oldOrderHeader.getCustomerId(), oldOrderHeader.getUid())
                .setValue(oldOrderHeader).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Order_getPendingMitraRef(oldOrderHeader.getPartyId(), oldOrderHeader.getUid())
                            .setValue(oldOrderBucket).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                if (lastAssignmentId != null && lastTechId != null)
                                    Assignment_delete(lastTechId, lastAssignmentId, null);

                                listener.onSuccess();
                            } else {
                                listener.onError(task.getException());
                            }
                        }
                    });
                } else {
                    listener.onError(task.getException());
                }
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
