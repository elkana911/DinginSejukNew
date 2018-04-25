package com.elkana.dslibrary.firebase;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.elkana.dslibrary.listener.ListenerDataExists;
import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerGetBasicInfo;
import com.elkana.dslibrary.listener.ListenerGetLong;
import com.elkana.dslibrary.listener.ListenerGetOrder;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.JobsAssigned;
import com.elkana.dslibrary.pojo.mitra.JobsCancelled;
import com.elkana.dslibrary.pojo.mitra.JobsHistory;
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
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Eric on 03-Mar-18.
 */

public class FBUtil {

    public static final String REF_ORDERS_CUSTOMER_AC_PENDING = "orders/ac/pending/customer";
    public static final String REF_ORDERS_MITRA_AC_PENDING = "orders/ac/pending/mitra";
    public static final String REF_ORDERS_CUSTOMER_AC_FINISHED = "orders/ac/finished/customer";
    public static final String REF_ORDERS_MITRA_AC_FINISHED = "orders/ac/finished/mitra";

    public static final String REF_ASSIGNMENTS_PENDING = "orders/ac/pending/teknisi";
//    public static final String REF_ASSIGNMENTS_PENDING = "assignments/ac/pending";

//    public static final String REF_ASSIGNMENTS_FIGHT = "assignments/ac/fight";    deprecated
    public static final String REF_MITRA_AC = "mitra/ac";
    public static final String REF_TECHNICIAN_AC = "technicians/ac";
    public static final String REF_CUSTOMER = "users";

    public static final String REF_MOVEMENTS= "movements";

    public static final String REF_MASTER_AC_SERVICE = "master/serviceType/airConditioner/subService";
    public static final String REF_MASTER_SERVERTIME = "master/mSetup/serverTime";

    public static final String FUNCTION_CREATE_ORDER = "createBooking";
    public static final String FUNCTION_CANCEL_ORDER = "cancelBooking";
    public static final String FUNCTION_RESCHEDULE_SERVICE = "rescheduleBooking";
    public static final String FUNCTION_TECHNICIAN_GRAB_ORDER = "grabOrder";
    public static final String FUNCTION_TECHNICIAN_START_OTW = "technicianStartOtw";
    public static final String FUNCTION_TECHNICIAN_START_WORKING = "technicianStartWorking";
    public static final String FUNCTION_REQUEST_STATUS_CHECK  = "requestStatusCheck";
    public static final String FUNCTION_MANUAL_ASSIGNMENT  = "manualAssignment";

    public static void IsPathExists(String completePath, final ListenerDataExists listener) {
        IsPathExists(FirebaseDatabase.getInstance().getReference(completePath), listener);
    }

    public static void IsPathExists(DatabaseReference path, final ListenerDataExists listener) {
        path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (listener == null)
                    return;

                if (dataSnapshot.exists())
                    listener.onFound();
                else
                    listener.onNotFound();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (listener != null)
                    listener.onError(databaseError.toException());
            }
        });
    }

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

    /*
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
    }*/

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

    public static DatabaseReference Assignment_getServiceItemsRef(String techId, String assignmentId) {
        return Assignment_getPendingRef(techId, assignmentId)
                .child("svcItems");
    }

    public static DatabaseReference Customer_GetRef(String userId) {

        return FirebaseDatabase.getInstance().getReference(REF_CUSTOMER)
                .child(userId);
    }


    /**
     * @param mitraId
     * @param customerId
     * @param orderId
     * @param assignmentId
     * @param techId       biasanya selalu ada kalau assignmentId != null
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
        keyValOrder.put(root + "/updatedStatusTimestamp", time);
        keyValOrder.put(root + "/updatedBy", updatedBy);
//        keyValOrder.put(root + "/updatedStatus", time);

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

        //TODO: harusnya check dulu apakah order msh exist spy tdk timbul data hantu
        IsPathExists(REF_ORDERS_CUSTOMER_AC_PENDING + "/" + customerId + "/" + orderId, new ListenerDataExists() {
            @Override
            public void onFound() {
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

            @Override
            public void onNotFound() {

            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    public static DatabaseReference Mitra_GetTechnicianRef(String mitraId, String technicianId) {
        return FirebaseDatabase.getInstance().getReference(REF_MITRA_AC)
                .child(mitraId)
                .child("technicians")
                .child(technicianId);

    }

    public static DatabaseReference Mitra_GetRef(String mitraId) {

        return FirebaseDatabase.getInstance().getReference(REF_MITRA_AC)
                .child(mitraId);
    }

    public static DatabaseReference Mitra_GetServicesRef(String mitraId) {
        return FirebaseDatabase.getInstance().getReference(REF_MITRA_AC)
                .child(mitraId)
                .child("services");
    }

    public static DatabaseReference Mitra_GetSSORef(String mitraId) {
        return FirebaseDatabase.getInstance().getReference(REF_MITRA_AC)
                .child(mitraId)
                .child("sso");
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

    public static void Order_updateValue(String customerId, String orderId, String key, Object value, final ListenerModifyData listener) {
        final Map<String, Object> keyVal = new HashMap<>();

        String root = REF_ORDERS_CUSTOMER_AC_PENDING + "/" + customerId + "/" + orderId;
        String node1 = root + "/" + key;
        keyVal.put(node1, value);

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
                listener.onError(new RuntimeException("To cancel Order, the expected status must either Cancel by Customer or Cancel by Server"));

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

    /* dipindah ke cloud
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

    }*/

    public static void TechnicianReg_setNotifyNewOrderTo(String techId, OrderBucket orderBucket, final ListenerModifyData listener) {

        if (TextUtils.isEmpty(techId)) {
            throw new RuntimeException("techId must not empty");
        }

        NotifyNewOrderItem item = new NotifyNewOrderItem();
        item.setAcCount(orderBucket.getAcCount());
        item.setAddress(orderBucket.getAddressByGoogle());
        item.setCustomerId(orderBucket.getCustomerId());
        item.setCustomerName(orderBucket.getCustomerName());
        item.setMitraId(orderBucket.getPartyId());
        item.setOrderId(orderBucket.getUid());
        item.setServiceTimestamp(orderBucket.getServiceTimestamp());    // this could be a problem
        item.setMitraTimestamp(new Date().getTime());                // this could be a problem

        // the problem is orderbucket information is not completed yet because technicianId is null when orderstatus=CREATED
        // so techId must be supplied
        item.setTechId(techId);

        try {
            Map<String, Object> keyVal = FBUtil.convertObjectToKeyVal(null, item);

            TechnicianReg_getNotifyNewOrderRef(item.getMitraId(), item.getTechId())
                    .child(item.getOrderId())
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
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static void TechnicianReg_deleteNotifyNewOrder(String mitraId, String techId, String orderId, final ListenerModifyData listener) {

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

    public static void TechnicianRegs_recursiveDeleteAllNotifyNewOrder(final String mitraId, final String orderId, final List<TechnicianReg> allTechnicianReg, int index, final ListenerModifyData listener) {

        TechnicianReg techReg;

        if (index < 0) {
            if (listener != null) {
                listener.onSuccess();
            }
            return;
        }

        techReg = allTechnicianReg.get(index);

        index -= 1;
        final int lastIndex = index;

        TechnicianReg_deleteNotifyNewOrder(mitraId, techReg.getTechId(), orderId, new ListenerModifyData() {
            @Override
            public void onSuccess() {
                TechnicianRegs_recursiveDeleteAllNotifyNewOrder(mitraId, orderId, allTechnicianReg, lastIndex, listener);
            }

            @Override
            public void onError(Exception e) {
                if (listener != null)
                    listener.onError(e);
            }
        });

    }

    public static Map<String, Object> convertObjectToKeyVal(String path, Object obj) throws IllegalAccessException {

        if (!TextUtils.isEmpty(path))
            path = Util.removeTrailingSlash(path);

        final Map<String, Object> keyValOrder = new HashMap<>();

        for (Field field : obj.getClass().getDeclaredFields()) {

            field.setAccessible(true);

            // otomatis key
            if (field.getName().equals("updatedTimestamp")
                    || field.getName().equals("timestamp")) {

                if (TextUtils.isEmpty(path)) {
                    keyValOrder.put(field.getName(), ServerValue.TIMESTAMP);
                } else {
                    keyValOrder.put(path + "/" + field.getName(), ServerValue.TIMESTAMP);
                }

            } else {

                if (TextUtils.isEmpty(path)) {
                    keyValOrder.put(field.getName(), field.get(obj));
                } else {
                    keyValOrder.put(path + "/" + field.getName(), field.get(obj));
                }
            }
        }

        return keyValOrder;

    }

    public static void TechnicianReg_isRegistered(String techId, String mitraId, final ListenerDataExists listener) {
        FirebaseDatabase.getInstance().getReference(REF_MITRA_AC)
                .child(mitraId)
                .child("technicians")
                .child(techId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (listener == null)
                    return;

                if (dataSnapshot.exists()) {
                    listener.onFound();
                    return;
                }

                listener.onNotFound();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (listener != null)
                    listener.onError(databaseError.toException());
            }
        });

    }

    public static DatabaseReference Template_CustomerProblemRef() {
        return FirebaseDatabase.getInstance().getReference("master/template_cust_problem");
    }

    /**
     * Cautions ! akan ada delay
     */
    public static void getTimestamp(final ListenerGetLong listener) {
        if (listener == null)
            return;

        FirebaseDatabase.getInstance().getReference(REF_MASTER_SERVERTIME)
                .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    FirebaseDatabase.getInstance().getReference(REF_MASTER_SERVERTIME)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        listener.onError(new RuntimeException("node timeserver not exists !"));
                                        return;
                                    }

                                    try {
                                        listener.onSuccess(dataSnapshot.getValue(Long.class));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        listener.onError(e);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    listener.onError(databaseError.toException());
                                }
                            });

                } else
                    listener.onError(task.getException());
            }
        });
    }

    public static void TechnicianReg_isConflictJob(String mitraId, String techId, String orderId) {
        // cek di jobhistory /jobs


        // cek di assignment

    }

    public static void Mitra_jobAsAssigned(final OrderBucket orderBucket) {
        Realm _r = Realm.getDefaultInstance();
        try {

            final String _wkt = Util.convertDateToString(new Date(orderBucket.getServiceTimestamp()), "yyyyMMddHHmm");
            JobsAssigned jobsAssigned = _r.where(JobsAssigned.class)
                    .equalTo("techId", orderBucket.getTechnicianId())
                    .equalTo("orderId", orderBucket.getUid())
                    .equalTo("wkt", _wkt)
                    .findFirst();

            if (jobsAssigned == null) {

                Mitra_GetTechnicianRef(orderBucket.getPartyId(), orderBucket.getTechnicianId())
                        .child("jobs_assigned")
                        .child(_wkt)
                        .setValue(orderBucket.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                            return;

                        Realm __r = Realm.getDefaultInstance();
                        try {
                            JobsAssigned _jobs = new JobsAssigned();
                            _jobs.setId(new Date().getTime());
                            _jobs.setOrderId(orderBucket.getUid());
                            _jobs.setTechId(orderBucket.getTechnicianId());
                            _jobs.setWkt(_wkt);

                            __r.beginTransaction();
                            __r.copyToRealmOrUpdate(_jobs);
                            __r.commitTransaction();
                        } finally {
                            __r.close();
                        }
                    }
                });
            }
        } finally {
            _r.close();
        }

    }

    public static void Mitra_jobAsCancelled(final OrderBucket orderBucket) {
        Realm _r = Realm.getDefaultInstance();
        try {

            final String _wkt = Util.convertDateToString(new Date(orderBucket.getServiceTimestamp()), "yyyyMMddHHmm");

            JobsCancelled jobsHistory = _r.where(JobsCancelled.class)
                    .equalTo("techId", orderBucket.getTechnicianId())
                    .equalTo("orderId", orderBucket.getUid())
                    .equalTo("wkt", _wkt)
                    .findFirst();

            if (jobsHistory == null) {

                Mitra_GetTechnicianRef(orderBucket.getPartyId(), orderBucket.getTechnicianId())
                        .child("jobs_cancelled")
                        .child(_wkt)
                        .setValue(orderBucket.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                            return;

                        //hapus job_assigned krena berarti sudah dikerjakan ? utk sementara disabled dulu krn msh perlu utk analisa scoring dan bentrok
//                                            FBUtil.Mitra_GetTechnicianRef(mMitraId, _orderBucket.getTechnicianId())
//                                                    .child("jobs_assigned")
//                                                    .child(_wkt)
//                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Realm ___r = Realm.getDefaultInstance();
//                                                        try{
//                                                            ___r.beginTransaction();
//                                                            ___r.where(JobsAssigned.class)
//                                                                    .equalTo("techId", _orderBucket.getTechnicianId())
//                                                                    .equalTo("orderId", _orderBucket.getUid())
//                                                                    .equalTo("wkt", _wkt)
//                                                                    .findAll().deleteAllFromRealm();
//                                                            ___r.commitTransaction();
//                                                        }finally {
//                                                            ___r.close();
//                                                        }
//                                                    }
//                                                }
//                                            });


                        Realm __r = Realm.getDefaultInstance();
                        try {
                            JobsCancelled _jobs = new JobsCancelled();
                            _jobs.setId(new Date().getTime());
                            _jobs.setOrderId(orderBucket.getUid());
                            _jobs.setTechId(orderBucket.getTechnicianId());
                            _jobs.setWkt(_wkt);

                            __r.beginTransaction();
                            __r.copyToRealmOrUpdate(_jobs);
                            __r.commitTransaction();
                        } finally {
                            __r.close();
                        }
                    }
                });
            }
        } finally {
            _r.close();
        }

    }
    // sementara ga bisa dipindah ke cloud krn status PAID hanya dilakukan oleh customer
    public static void Mitra_jobAsHistory(final OrderBucket orderBucket) {
        Realm _r = Realm.getDefaultInstance();
        try {

            final String _wkt = Util.convertDateToString(new Date(orderBucket.getServiceTimestamp()), "yyyyMMddHHmm");

            JobsHistory jobsHistory = _r.where(JobsHistory.class)
                    .equalTo("techId", orderBucket.getTechnicianId())
                    .equalTo("orderId", orderBucket.getUid())
                    .equalTo("wkt", _wkt)
                    .findFirst();

            if (jobsHistory == null) {

                Mitra_GetTechnicianRef(orderBucket.getPartyId(), orderBucket.getTechnicianId())
                        .child("jobs_history")
                        .child(_wkt)
                        .setValue(orderBucket.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                            return;

                        //hapus job_assigned krena berarti sudah dikerjakan ? utk sementara disabled dulu krn msh perlu utk analisa scoring dan bentrok
//                                            FBUtil.Mitra_GetTechnicianRef(mMitraId, _orderBucket.getTechnicianId())
//                                                    .child("jobs_assigned")
//                                                    .child(_wkt)
//                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Realm ___r = Realm.getDefaultInstance();
//                                                        try{
//                                                            ___r.beginTransaction();
//                                                            ___r.where(JobsAssigned.class)
//                                                                    .equalTo("techId", _orderBucket.getTechnicianId())
//                                                                    .equalTo("orderId", _orderBucket.getUid())
//                                                                    .equalTo("wkt", _wkt)
//                                                                    .findAll().deleteAllFromRealm();
//                                                            ___r.commitTransaction();
//                                                        }finally {
//                                                            ___r.close();
//                                                        }
//                                                    }
//                                                }
//                                            });


                        Realm __r = Realm.getDefaultInstance();
                        try {
                            JobsHistory _jobs = new JobsHistory();
                            _jobs.setId(new Date().getTime());
                            _jobs.setOrderId(orderBucket.getUid());
                            _jobs.setTechId(orderBucket.getTechnicianId());
                            _jobs.setWkt(_wkt);

                            __r.beginTransaction();
                            __r.copyToRealmOrUpdate(_jobs);
                            __r.commitTransaction();
                        } finally {
                            __r.close();
                        }
                    }
                });
            }
        } finally {
            _r.close();
        }
    }

    public static void Technician_addToken(final String userId, final String token) {
        if (TextUtils.isEmpty(token))
            return;

        Technician_GetRef(userId)
                .child("firebaseToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                };

                List<String> messages = dataSnapshot.getValue(t);

                boolean exist = false;

                if (messages == null)
                    messages = new ArrayList<String>();

                for (String s: messages) {
                    if (s.equals(token)) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    // replace
                    messages.clear();
                    messages.add(token);

                    Technician_GetRef(userId)
                            .child("firebaseToken").setValue(messages);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

                /*
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
    }

    public static void Mitra_addToken(final String mitraId, final String token) {
        if (TextUtils.isEmpty(token))
            return;


        Mitra_GetRef(mitraId)
                .child("firebaseToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                };

                List<String> messages = dataSnapshot.getValue(t);

                boolean exist = false;

                if (messages == null)
                    messages = new ArrayList<String>();

                for (String s: messages) {
                        if (s.equals(token)) {
                            exist = true;
                            break;
                        }
                }

                if (!exist) {
                    // replace
                    messages.clear();
                    messages.add(token);

                    Mitra_GetRef(mitraId)
                            .child("firebaseToken").setValue(messages);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void Customer_addToken(final String userId, final String token) {
        if (TextUtils.isEmpty(token))
            return;

        Customer_GetRef(userId)
                .child("firebaseToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                };

                List<String> messages = dataSnapshot.getValue(t);

                boolean exist = false;

                if (messages == null)
                    messages = new ArrayList<String>();
                for (String s: messages) {
                    if (s.equals(token)) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    // replace
                    messages.clear();
                    messages.add(token);

                    Customer_GetRef(userId)
                            .child("firebaseToken").setValue(messages);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static DatabaseReference Technician_GetRef(String userId) {

        return FirebaseDatabase.getInstance().getReference(REF_TECHNICIAN_AC)
                .child(userId);
    }

    public static String friendlyTaskNotSuccessfulMessage(Exception exception) {
        if (exception instanceof SocketTimeoutException)
            return "Timeout Connection. Check your network.";
        else
            return exception.getMessage();
    }
}
