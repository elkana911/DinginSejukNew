package com.elkana.customer.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.elkana.customer.R;
import com.elkana.customer.pojo.MobileSetup;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerSync;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.mitra.TmpMitra;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.NetUtil;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Eric on 23-Oct-17.
 */

public class CustomerUtil {
    public static final String TAG = CustomerUtil.class.getSimpleName();
    public static final String REF_MITRA_AC = "mitra/ac";

    public static final String REF_ORDERS_AC_PENDING = "orders/ac/pending";
    public static final String REF_ORDERS_AC_FINISHED = "orders/ac/finished";

    public static final String REF_ORDERS_CUSTOMER_AC_PENDING = REF_ORDERS_AC_PENDING + "/customer";
    public static final String REF_ORDERS_MITRA_AC_PENDING = REF_ORDERS_AC_PENDING + "/mitra";

    public static final String REF_MOVEMENTS = "movements";
    public static final String REF_MASTER_SETUP = "master/mSetup/" + Const.CONFIG_AS_COSTUMER;

    public static void getOnlineDataToOffline() {

    }
/*
udah di taruh di lib
    public static void cleanTransactionData() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.where(TmpMitra.class).findAll().deleteAllFromRealm();
            realm.where(Mitra.class).findAll().deleteAllFromRealm();
//            realm.where(OrderDetail.class).findAll().deleteAllFromRealm();
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
*/

    public static void initiateOfflineData() {

        Realm r = Realm.getDefaultInstance();
        try {
            r.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    MobileSetup mobileSetup = new MobileSetup();
                    mobileSetup.setId(Const.CONFIG_AS_COSTUMER);   // hardcode as Customer setup

                    mobileSetup.setMap_show_vendor_title(true);
                    mobileSetup.setGps_mandatory(true);
                    mobileSetup.setTheme_color_default("#143664");
                    mobileSetup.setTheme_color_default_accent("#00f73e");
                    mobileSetup.setTheme_color_default_inactive("#000000");

                    mobileSetup.setVendor_radius_km(10);
                    mobileSetup.setUnit_ac_max(7);
                    mobileSetup.setTimeout_cancel_minute(Util.TESTING_MODE ? 2 : 30);
                    realm.copyToRealmOrUpdate(mobileSetup);

//                    realm.copyToRealmOrUpdate(new ServiceType(10, "Air Conditioner", "Air Conditioner"));
//                    realm.copyToRealmOrUpdate(new ServiceType(20, "Electricity", "PLN/Listrik"));
//
//                    realm.copyToRealmOrUpdate(new SubServiceType(10, "Cleaning", "Bersihin AC"));
//                    realm.copyToRealmOrUpdate(new SubServiceType(20, "Freon Charge", "Freon Charge"));
//                    realm.copyToRealmOrUpdate(new SubServiceType(30, "Installation", "Instalasi"));

                }
            });
        } finally {
            r.close();
        }

    }

    public static String getServiceTypeLabel(Context ctx, int serviceType) {
        switch (serviceType) {
            case Const.SERVICE_TYPE_QUICK:
                return ctx.getString(R.string.title_activity_ac_quickservice);
            case Const.SERVICE_TYPE_SCHEDULED:
                return ctx.getString(R.string.title_activity_ac_schedservice);
            default:
                throw new RuntimeException("Unknown serviceType=" + serviceType);
        }
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
            case PAID:
                return ctx.getString(R.string.status_paid);
            case CANCELLED_BY_CUSTOMER:
                return ctx.getString(R.string.status_cancelled_by_customer);
            case CANCELLED_BY_SERVER:
                return ctx.getString(R.string.status_cancelled_by_server);
            case CANCELLED_BY_TIMEOUT:
                return ctx.getString(R.string.status_cancelled_by_timeout);
//            case RESCHEDULED:
//                return ctx.getString(R.string.status_rescheduled);
            default:
                return "Unknown Status";
        }
    }

    public static Mitra lookUpMitraById(Realm realm, String mitraId) {
        Mitra mitraObj = realm.where(Mitra.class)
                .equalTo("uid", mitraId)
                .findFirst();

        return mitraObj;
    }

    public static Mitra lookUpMitra(Realm realm, String name) {
        Mitra mitraObj = realm.where(Mitra.class)
                .equalTo("name", name)
                .findFirst();

        return mitraObj;
    }

    public static Mitra lookUpMitraByName(String name) {
        Realm r = Realm.getDefaultInstance();
        try {
            return lookUpMitra(r, name);
        } finally {
            r.close();
        }
    }

    public static Mitra lookUpMitraById(String mitraId) {
        Realm r = Realm.getDefaultInstance();
        try {
            return lookUpMitraById(r, mitraId);
        } finally {
            r.close();
        }
    }

    public static void syncMitra(Context ctx, final ListenerSync listener) {

        if (!NetUtil.isConnected(ctx)) {

            if (listener != null)
                listener.onPostSync(null);

            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(REF_MITRA_AC);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Realm r = Realm.getDefaultInstance();
                    try {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Object _obj = postSnapshot.getValue();
                            Log.e(TAG, _obj.toString());

//                        long childrenCount = postSnapshot.getChildrenCount();

                            for (DataSnapshot _sub : postSnapshot.getChildren()) {

                                if (_sub.getKey().equals("basicInfo")) {
                                    Mitra __obj = _sub.getValue(Mitra.class);
                                    Log.e(TAG, __obj.toString());

                                    r.beginTransaction();
                                    r.copyToRealmOrUpdate(__obj);
                                    r.commitTransaction();
                                }

                            }
                        }

                    } finally {
                        r.close();
                    }

//                    GenericTypeIndicator<ArrayList<Mitra>> t = new GenericTypeIndicator<ArrayList<Mitra>>() {
//                    };
//                    final List<Mitra> list = (List<Mitra>) dataSnapshot.getValue(t);
//
//                    Realm r = Realm.getDefaultInstance();
//                    try {
//                        r.beginTransaction();
//                        for (Mitra mitra : list) {
//                            r.copyToRealmOrUpdate(mitra);
//                        }
//                        r.commitTransaction();
//                    } finally {
//                        r.close();
//                    }

                }

                if (listener != null)
                    listener.onPostSync(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                if (listener != null)
                    listener.onPostSync(databaseError.toException());

                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    public static TmpMitra cloneMitra(Mitra mitra) {
        TmpMitra m = new TmpMitra();
        m.setUid(mitra.getUid());
        m.setName(mitra.getName());
        m.setStatus(mitra.getStatus());

        m.setWorkingHourStart(mitra.getWorkingHourStart());
        m.setWorkingHourEnd(mitra.getWorkingHourEnd());
        m.setRating(mitra.getRating());
        m.setEnable(mitra.isEnable());
        m.setVisible(mitra.isVisible());

        m.setAddressLabel(mitra.getAddressLabel());
        m.setAddressByGoogle(mitra.getAddressByGoogle());
        m.setEmail(mitra.getEmail());
        m.setLatitude(mitra.getLatitude());
        m.setLongitude(mitra.getLongitude());
        m.setPhone1(mitra.getPhone1());
        m.setFax1(mitra.getFax1());
        m.setPhone2(mitra.getPhone2());
        m.setFax2(mitra.getFax2());
        m.setPartyTypeId(mitra.getPartyTypeId());

        m.setUserType(mitra.getUserType());
        m.setCreatedTimestamp(mitra.getCreatedTimestamp());
        m.setUpdatedTimestamp(mitra.getUpdatedTimestamp());

        return m;
    }

    private static void recursiveGetPendingMitra(final List<String> mitraList, int index, final ListenerSync listener) {

        String mitraId;

        if (index < 0) {
            if (listener != null) {
                listener.onPostSync(null);
            }
            return;
        }

        mitraId = mitraList.get(index);

        index -= 1;
        final int lastIndex = index;
        FirebaseDatabase.getInstance().getReference(REF_ORDERS_MITRA_AC_PENDING)
                .child(mitraId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Realm r = Realm.getDefaultInstance();
                    try {
                        r.beginTransaction();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            OrderBucket _obj = postSnapshot.getValue(OrderBucket.class);

                            r.copyToRealmOrUpdate(_obj);
                            Log.e(TAG, _obj.toString());
                        }

                        r.commitTransaction();
                    } finally {
                        r.close();
                    }
                }

                recursiveGetPendingMitra(mitraList, lastIndex, listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (listener != null)
                    listener.onPostSync(databaseError.toException());
            }
        });

    }

    public static void syncOrders(Context ctx, String customerId, final ListenerSync listener) {
        if (!NetUtil.isConnected(ctx)) {

            if (listener != null)
                listener.onPostSync(null);

            return;
        }

        FirebaseDatabase.getInstance().getReference(REF_ORDERS_CUSTOMER_AC_PENDING)
                .child(customerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<String> mitraList = new ArrayList<>();

                        if (dataSnapshot.exists()) {
                            Realm r = Realm.getDefaultInstance();
                            try {
                                r.beginTransaction();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    OrderHeader _obj = postSnapshot.getValue(OrderHeader.class);

                                    mitraList.add(_obj.getPartyId());

                                    r.copyToRealmOrUpdate(_obj);
                                    Log.e(TAG, _obj.toString());
                                }

                                r.commitTransaction();
                            } finally {
                                r.close();
                            }
                        }

                        if (mitraList.size() > 0) {
                            recursiveGetPendingMitra(mitraList, mitraList.size() - 1, listener);
                        } else {
                            if (listener != null)
                                listener.onPostSync(null);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (listener != null)
                            listener.onPostSync(databaseError.toException());

                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });

    }

    public static void updateOrderStatus(String orderId, String userId, EOrderDetailStatus status, final ListenerModifyData listener) {
        Map<String, Object> keyVal = new HashMap<>();
        keyVal.put("statusDetailId", status.name());

        if (status == EOrderDetailStatus.CANCELLED_BY_TIMEOUT) {
            keyVal.put("statusId", EOrderStatus.FINISHED.name());
        } else {
            keyVal.put("statusId", EOrderStatus.PENDING.name());
        }

        FirebaseDatabase.getInstance().getReference(CustomerUtil.REF_ORDERS_CUSTOMER_AC_PENDING)
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

    public static void syncUserInformation(Realm realm) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (currentUser == null) {
            Log.e(TAG, "Please login to synchronize User Information");
            return;
        }

        BasicInfo basicInfo = realm.where(BasicInfo.class)
                .equalTo("uid", currentUser.getUid())
                .findFirst();

        if (basicInfo == null) {
            database.getReference("users/" + currentUser.getUid()).child("basicInfo")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                return;
                            }

                            final BasicInfo _obj = dataSnapshot.getValue(BasicInfo.class);

                            Realm r = Realm.getDefaultInstance();
                            try {
                                r.beginTransaction();
                                r.copyToRealmOrUpdate(_obj);
                                r.commitTransaction();

                            } finally {
                                r.close();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        }
                    });
        }

//        if (realm.where(UserAddress.class)
//                .count() < 1) {
        database.getReference("users/" + currentUser.getUid()).child("address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        Realm r = Realm.getDefaultInstance();
                        try {
                            r.beginTransaction();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                UserAddress _obj = postSnapshot.getValue(UserAddress.class);

                                r.copyToRealmOrUpdate(_obj);
                                Log.e(TAG, _obj.toString());
                            }

                            r.commitTransaction();
                        } finally {
                            r.close();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });
//        }

        // sync setup
        database.getReference(CustomerUtil.REF_MASTER_SETUP)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final MobileSetup mobileSetup = dataSnapshot.getValue(MobileSetup.class);

                        Realm r = Realm.getDefaultInstance();
                        try {
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(mobileSetup);
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

        //tokens ga boleh menyimpan token dari device lain. token secara default diperoleh di MyFirebaseInstanceIDService

    }


    /*
    public void moveFirebaseRecord(Firebase fromPath, final Firebase toPath)
    {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                toPath.setValue(dataSnapshot.getValue(), new Firebase.CompletionListener()
                {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase)
                    {
                        if (firebaseError != null)
                        {
                            System.out.println("Copy failed");
                        }
                        else
                        {
                            System.out.println("Success");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError)
            {
                System.out.println("Copy failed");
            }
        });
    }
*/

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
        try {
            MobileSetup first = r.where(MobileSetup.class).findFirst();
            if (first == null)
                throw new NullPointerException("Unable to get table MobileSetup");

            return r.copyFromRealm(first);
        } finally {
            r.close();
        }
    }

//    public static boolean isExpiredBooking(OrderHeader order) {
//        return Util.isExpiredBooking(order.getTimestamp(), getMobileSetup().getLastOrderMinutes());
//    }

}
