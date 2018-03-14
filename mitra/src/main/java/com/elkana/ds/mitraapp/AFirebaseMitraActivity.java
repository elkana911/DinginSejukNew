package com.elkana.ds.mitraapp;

import android.support.annotation.NonNull;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.exception.OrderAlreadyFinished;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetOrder;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eric on 13-Mar-18.
 */

public abstract class AFirebaseMitraActivity extends FirebaseActivity{
    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }

    @Override
    protected void onLoggedOff() {

    }

    protected void Assignment_create(final String techId, final String techName, final String custId, final String orderId, final ListenerModifyData listener) {

        //1. get orderHeader
        FBUtil.Orders_getPendingCustomerRef(custId, orderId, new ListenerGetOrder() {
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

                EOrderDetailStatus _newStatus = EOrderDetailStatus.ASSIGNED;

                FirebaseDatabase _firebaseDatabase = FirebaseDatabase.getInstance();

                DatabaseReference refAssignment = _firebaseDatabase.getReference(FBUtil.REF_ASSIGNMENTS_PENDING)
                        .child(techId)
                        .push();

                /*
                    assignment node
                    assignments/ac/pending/<technicianId>/<assignmentId>/assign/statusDetailId
                 */
                final Assignment assignment = new Assignment();

                assignment.setUid(refAssignment.getKey());

                String customerId = obj.getCustomerId();

                final Map<String, Object> keyValOrder = new HashMap<>();
                /*
                    customer node
                    orders/ac/pending/customer/<customerId>/<orderId>/statusDetailId
                 */
                String _root_node = FBUtil.REF_ORDERS_CUSTOMER_AC_PENDING + "/" + customerId + "/" + orderId;
                keyValOrder.put(_root_node + "/assignmentId", assignment.getUid());
                keyValOrder.put(_root_node + "/technicianId", techId);
                keyValOrder.put(_root_node + "/technicianName", techName);
                keyValOrder.put(_root_node + "/assignmentId", refAssignment.getKey());
                keyValOrder.put(_root_node + "/statusDetailId", _newStatus.name());
                keyValOrder.put(_root_node + "/updatedTimestamp", ServerValue.TIMESTAMP);
                keyValOrder.put(_root_node + "/updatedBy", String.valueOf(Const.USER_AS_MITRA));

                /*
                    mitra node
                    orders/ac/pending/mitra/<mitraId>/<orderId>/statusDetailId
                 */
                _root_node = FBUtil.REF_ORDERS_MITRA_AC_PENDING + "/" + obj.getPartyId() + "/" + orderId;
                keyValOrder.put(_root_node + "/assignmentId", assignment.getUid());
                keyValOrder.put(_root_node + "/statusDetailId", _newStatus.name());
                keyValOrder.put(_root_node + "/technicianId", techId);
                keyValOrder.put(_root_node + "/technicianName", techName);
                keyValOrder.put(_root_node + "/assignmentId", refAssignment.getKey());
                keyValOrder.put(_root_node + "/updatedTimestamp", ServerValue.TIMESTAMP);
                keyValOrder.put(_root_node + "/updatedBy", String.valueOf(Const.USER_AS_MITRA));

                assignment.setTechnicianId(techId);
                assignment.setTechnicianName(techName);
                assignment.setDateOfService(obj.getDateOfService());
                assignment.setTimeOfService(obj.getTimeOfService());
                assignment.setStatusDetailId(_newStatus.name());
                assignment.setUpdatedBy(String.valueOf(Const.USER_AS_MITRA));
                assignment.setCustomerAddress(obj.getAddressByGoogle());
                assignment.setCustomerId(obj.getCustomerId());
                assignment.setCustomerName(obj.getCustomerName());
                assignment.setLatitude(obj.getLatitude());
                assignment.setLongitude(obj.getLongitude());
                assignment.setOrderId(obj.getUid());
                assignment.setMitraId(obj.getPartyId());
                assignment.setMitraName(obj.getPartyName());

                // unavoided timestamp using java.util.Date
                assignment.setUpdatedTimestamp(new Date().getTime());
                assignment.setCreatedDate(new Date().getTime());

                _root_node = FBUtil.REF_ASSIGNMENTS_PENDING + "/" + techId + "/" + assignment.getUid();
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

}
