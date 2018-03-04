package com.elkana.dslibrary.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;

/**
 * Created by Eric on 19-Oct-17.
 */

public abstract class FirebaseActivity extends BasicActivity {

    private static final String TAG = "FirebaseActivity";

    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onLoggedOn(user);
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    onLoggedOff();
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }

    protected abstract void onLoggedOff();

    protected abstract void onLoggedOn(FirebaseUser user);

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    protected void cleanTransactionData(){
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();

            realm.where(OrderHeader.class).findAll().deleteAllFromRealm();
            realm.where(OrderBucket.class).findAll().deleteAllFromRealm();

            realm.where(BasicInfo.class).findAll().deleteAllFromRealm();
            realm.where(FirebaseToken.class).findAll().deleteAllFromRealm();
            realm.where(UserAddress.class).findAll().deleteAllFromRealm();
//            realm.deleteAll(); bahaya krn mainmenu jg ikut kehapus
            realm.commitTransaction();
        } finally {
            realm.close();
        }
    }

    protected void logout() {
        FirebaseAuth.getInstance().signOut();
//        LoginManager.getInstance().logOut();

//        DataUtil.cleanTransactionData();
        cleanTransactionData();

    }

/*
    protected DatabaseReference getOrderByCustomer_PendingRef(String customerId, String orderId) {
        return FirebaseDatabase.getInstance().getReference(DataUtil.REF_ORDERS_CUSTOMER_AC_PENDING)
                .child(customerId)
                .child(orderId);

    }

    protected DatabaseReference getOrderByMitra_PendingRef(String mitraId, String orderId) {
        return FirebaseDatabase.getInstance().getReference(DataUtil.REF_ORDERS_MITRA_AC_PENDING)
                .child(mitraId)
                .child(orderId);

    }

    protected void firebaseGetAllMitra(final ListenerGetAllData listener) {
        database.getReference(DataUtil.REF_MITRA_AC)
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

    public void firebaseOrder_SetStatus(final String mitraId, final String customerId, final String orderId, EOrderDetailStatus newStatus, final ListenerModifyData listener) {

        final Map<String, Object> keyValOrder = new HashMap<>();
        keyValOrder.put("statusDetailId", newStatus.name());
        keyValOrder.put("updatedTimestamp", new Date().getTime());

        // fyi ada 3 node yg hrs diupdate:
//1.        orders/ac/pending/customer/<customerId>/<orderId>/statusDetailId
//2.        assignments/ac/pending/<technicianId>/<assignmentId>/assign/statusDetailId
//3.        orders/ac/pending/mitra/<mitraId>/<orderId>/statusDetailId
        //get last status first just in case something wrng will revert
        getOrderByCustomer_PendingRef(customerId, orderId)
                .child("statusDetailId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String previousValue = dataSnapshot.getValue(String.class);

                // 2. update mitra
                getOrderByMitra_PendingRef(mitraId, orderId)
                        .updateChildren(keyValOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // 1. update customer
                            getOrderByCustomer_PendingRef(customerId, orderId)
                                    .updateChildren(keyValOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        if (listener != null)
                                            listener.onSuccess();
                                    } else {

                                        //revert statusdetail in node mitra
                                        getOrderByMitra_PendingRef(mitraId, orderId)
                                                .child("statusDetailId").setValue(previousValue);

                                        if (listener != null)
                                            listener.onError(task.getException());
                                    }
                                }
                            });


                        } else {

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
*/
    /*
    https://stackoverflow.com/questions/38652007/how-to-retrieve-specific-list-of-data-from-firebase
    Contoh Get List:
                ref.child("mobile_setup/theme/setup1").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, MainMenu> value = (Map<String, MainMenu>) dataSnapshot.getValue();

                        for (Map.Entry<String, MainMenu> entry : value.entrySet()) {
                            Log.e(TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        }
                    }
                });

    contoh get list of string
        database.getReference("users/" + mAuth.getCurrentUser().getUid()).child("firebaseToken")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                        final List<String> list = (List<String>) dataSnapshot.getValue(t);

                        ..do something...
                    }
                });

    Contoh Get List paling singkat:
                ref.child("mobile_setup/theme/setup1").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // krn mau ambil list pastikan loop di getChildren, kalo cuma item gunakan Contoh Get Object
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            MainMenu _menu = postSnapshot.getValue(MainMenu.class);
                            Log.e(TAG, _menu.toString());
                        }
                        ....


    Contoh Get Object:
                ref.child("mobile_setup").child("theme").child("setup1").child("item1").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        MainMenu value = dataSnapshot.getValue(MainMenu.class);
                        Log.e(TAG, value.toString());   // Air Conditioner
                    }
                });


Contoh set values:
        Map<String, Object> keyVal = new HashMap<>();
        keyVal.put("ratingByCustomer", rating);
        keyVal.put("ratingComments", comments);
        keyVal.put("statusId", EOrderStatus.FINISHED.name());

        orderHeaderPendingRef.updateChildren(keyVal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                cardReview.setVisibility(View.GONE);
                tvStatusDetil.setText(tvStatusDetil.getText() + "\n" + getString(R.string.message_review_given));

            }
        });



     */
}
