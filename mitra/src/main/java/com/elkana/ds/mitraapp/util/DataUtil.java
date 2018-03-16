package com.elkana.ds.mitraapp.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.pojo.MobileSetup;
import com.elkana.ds.mitraapp.pojo.NotifyTechnician;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.mitra.TechnicianReg;
import com.elkana.dslibrary.pojo.mitra.TmpMitra;
import com.elkana.dslibrary.pojo.technician.Technician;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Eric on 23-Oct-17.
 */

public class DataUtil {
    public static final String TAG = DataUtil.class.getSimpleName();
    public static final String REF_VENDORS_AC = "master/party/supplierAC";
//    public static final String REF_ORDERS_AC = "orders/ac/orderHeader";

    public static final String REF_ORDERS_CUSTOMER_AC_PENDING = "orders/ac/pending/customer";
    public static final String REF_ORDERS_MITRA_AC_PENDING = "orders/ac/pending/mitra";
    public static final String REF_ORDERS_AC_FINISHED = "orders/ac/finished";

    public static final String REF_ASSIGNMENTS = "assignments/ac";
//    public static final String REF_MITRA_AC = "mitra/ac";

    public static final String REF_SUBSERVICEAC = "master/serviceType/airConditioner/subService";   // biasanya dipakai di HQ utk available service for all mitra. tp tetep diunduh ke teknisi karena akan dimapping dengan serviceToParty
    public static final String REF_VENDOR_AC_SERVICES = "serviceToParty/ac";

    public static final String REF_TECHNICIAN_AC = "technicians/ac";

    public static final String REF_ASSIGNMENTS_PENDING = "assignments/ac/pending";
    public static final String REF_ASSIGNMENTS_FINISHED = "assignments/ac/finished";

    public static final String REF_MOVEMENTS= "movements";
    public static final String REF_MASTER_SETUP = "master/mSetup/" + Const.CONFIG_AS_MITRA;

    public static void getOnlineDataToOffline() {

    }

    public static void cleanTransactionData() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();

            realm.where(NotifyTechnician.class).findAll().deleteAllFromRealm();
            realm.where(TechnicianReg.class).findAll().deleteAllFromRealm();
            realm.where(Mitra.class).findAll().deleteAllFromRealm();
//            realm.where(TmpMitra.class).findAll().deleteAllFromRealm();
            realm.where(Assignment.class).findAll().deleteAllFromRealm();
//            realm.deleteAll(); bahaya krn mainmenu jg ikut kehapus
            realm.commitTransaction();
        } finally {
            realm.close();
        }
    }

    public static void initiateOfflineData() {

        Realm r = Realm.getDefaultInstance();
        try {
            r.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    MobileSetup mobileSetup = new MobileSetup();
                    mobileSetup.setId(Const.CONFIG_AS_MITRA);   // hardcode as Mitra Setup

                    mobileSetup.setGps_mandatory(true);
                    mobileSetup.setTheme_color_default("#143664");
                    mobileSetup.setTheme_color_default_accent("#00f73e");
                    mobileSetup.setTheme_color_default_inactive("#000000");

                    realm.copyToRealmOrUpdate(mobileSetup);


                }
            });
        } finally {
            r.close();
        }
    }

    public static String getServiceTypeLabel(Context ctx, int serviceType) {
        return serviceType < 2 ? ctx.getString(R.string.title_activity_ac_quickservice)
                : ctx.getString(R.string.title_activity_ac_schedservice);
    }

    public static String getMessageStatusDetail(Context ctx, EOrderDetailStatus status) {

        switch (status) {
            case CREATED:
            case UNHANDLED:
                return ctx.getString(R.string.status_created);
            case ASSIGNED:
                return ctx.getString(R.string.status_assigned);
            case OTW:
                return ctx.getString(R.string.status_otw);
            case WORKING:
                return ctx.getString(R.string.status_working);
            case PAYMENT:
                return ctx.getString(R.string.status_payment);
            case CANCELLED_BY_CUSTOMER:
                return ctx.getString(R.string.status_cancelled_by_customer);
            case CANCELLED_BY_SERVER:
                return ctx.getString(R.string.status_cancelled_by_server);
            case CANCELLED_BY_TIMEOUT:
                return ctx.getString(R.string.status_cancelled_by_timeout);
            /*case RESCHEDULED:
                return ctx.getString(R.string.status_rescheduled);*/
            default:
                return "Unknown Status";
        }
    }

    public static Technician lookUpTechnicianByUid(Realm realm, String uid) {
        Technician obj = realm.where(Technician.class)
                .equalTo("uid", uid)
                .findFirst();

        return obj;
    }

    public static Technician lookUpTechnician(Realm realm, String name) {
        Technician obj = realm.where(Technician.class)
                .equalTo("name", name)
                .findFirst();

        return obj;
    }

    public static List<TechnicianReg> getAllTechnicianReg() {
        Realm r = Realm.getDefaultInstance();
        try {
            return r.copyFromRealm(r.where(TechnicianReg.class).findAll());
        } finally {
            r.close();
        }
    }


    public static int isWorkingHour(int offset) {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, offset);

        int x = c.get(Calendar.HOUR_OF_DAY);

        if (x < 8) {
            x = 8;
        } else if (x > 17) {
            x = 8;
        }
        return x;

    }

    public static Date getWorkingDay(Date from, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);

        c.add(Calendar.DAY_OF_MONTH, offset);

        // kalo jatuhnya hr minggu skip ke senin
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }


        return c.getTime();
    }

    public static MobileSetup getMobileSetup() {
        Realm r = Realm.getDefaultInstance();
        try{
            return r.copyFromRealm(r.where(MobileSetup.class).findFirst());
        }finally {
            r.close();
        }
    }

    public static void updateOrderStatus(String orderId, String userId, EOrderDetailStatus status, final ListenerModifyData listener) {
        Map<String, Object> keyVal = new HashMap<>();
        keyVal.put("statusDetailId", status.name());

        if (status == EOrderDetailStatus.CANCELLED_BY_TIMEOUT) {
            keyVal.put("statusId", EOrderStatus.FINISHED.name());
        } else {
            keyVal.put("statusId", EOrderStatus.PENDING.name());
        }

        FirebaseDatabase.getInstance().getReference(DataUtil.REF_ORDERS_CUSTOMER_AC_PENDING)
                .child(userId)
                .child(orderId).updateChildren(keyVal).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    public static boolean isExpiredOrder(OrderHeader order) {
        return isExpiredOrder(order.getTimestamp());
    }

    public static boolean isExpiredOrder(long timestamp) {
        return Util.isExpiredOrder(timestamp, getMobileSetup().getLastOrderMinutes());
    }
*/
    public static void syncTechnicianReg(){
        String mitraId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference(FBUtil.REF_MITRA_AC)
                .child(mitraId)
                .child("technicians")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        final List<TechnicianReg> list = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            list.add(postSnapshot.getValue(TechnicianReg.class));
                        }

                        Realm r = Realm.getDefaultInstance();
                        try{
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(list);
                                }
                            });
                        }finally {
                            r.close();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });
    }

    public static void syncUserInformation() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // get user info
        DatabaseReference refUser = database.getReference(FBUtil.REF_MITRA_AC).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        refUser.child("basicInfo")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
//                            logout();

                            return;
                        }

                        final BasicInfo basicInfo = dataSnapshot.getValue(BasicInfo.class);

                        Realm r = Realm.getDefaultInstance();
                        try {
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(basicInfo);
                                }
                            });
                        } finally {
                            r.close();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });

        refUser.child("address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        final List<UserAddress> list = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            list.add(postSnapshot.getValue(UserAddress.class));
                        }

                        Realm r = Realm.getDefaultInstance();
                        try{
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(list);
                                }
                            });
                        }finally {
                            r.close();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });

        refUser.child("firebaseToken")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        final List<FirebaseToken> list = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String ss = postSnapshot.getValue(String.class);
                            FirebaseToken ft = new FirebaseToken();
                            ft.setToken(ss);
                            list.add(ft);
                        }
                        Realm r = Realm.getDefaultInstance();
                        try{
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(list);
                                }
                            });
                        }finally {
                            r.close();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });


        database.getReference(DataUtil.REF_MASTER_SETUP)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final MobileSetup mobileSetup = dataSnapshot.getValue(MobileSetup.class);

                        Realm r = Realm.getDefaultInstance();
                        try{
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(mobileSetup);
                                }
                            });

                        }finally {
                            r.close();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });

    }

}
