package com.elkana.ds.mitraapp.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.pojo.MobileSetup;
import com.elkana.ds.mitraapp.pojo.NotifyTechnician;
import com.elkana.dslibrary.pojo.mitra.JobsAssigned;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.mitra.SubServiceType;
import com.elkana.dslibrary.pojo.mitra.TechnicianReg;
import com.elkana.dslibrary.pojo.technician.Technician;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Eric on 23-Oct-17.
 */

public class MitraUtil {
    public static final String TAG = MitraUtil.class.getSimpleName();
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
            realm.where(JobsAssigned.class).findAll().deleteAllFromRealm();
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

    public static List<TechnicianReg> getAllTechnicianReg(boolean sortByScore) {
        Realm r = Realm.getDefaultInstance();
        try {
            RealmResults<TechnicianReg> all = r.where(TechnicianReg.class).findAll();

            return r.copyFromRealm(sortByScore ? all.sort("lastScore", Sort.DESCENDING) : all);
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
        try {
            return r.copyFromRealm(r.where(MobileSetup.class).findFirst());
        } finally {
            r.close();
        }
    }

    // fungsi yg cukup bahaya. hanya boleh dipake di mitra yg jamnya lebih akurat
    public static boolean isExpiredBooking(OrderHeader orderHeader) {
        EOrderDetailStatus status = EOrderDetailStatus.convertValue(orderHeader.getStatusDetailId());
        if (status == EOrderDetailStatus.CREATED
                || status == EOrderDetailStatus.ASSIGNED
                || status == EOrderDetailStatus.UNHANDLED
                /*|| status == EOrderDetailStatus.RESCHEDULED*/
                || status == EOrderDetailStatus.UNKNOWN
                ) {
            return DateUtil.isExpiredTime(orderHeader.getServiceTimestamp(), 30) > 0;   //hardcode 30

        } else
            return false;
    }

    // fungsi yg cukup bahaya. hanya boleh dipake di mitra yg jamnya lebih akurat
    public static boolean isExpiredBooking(OrderBucket orderBucket) {
        EOrderDetailStatus status = EOrderDetailStatus.convertValue(orderBucket.getStatusDetailId());

        if (status == EOrderDetailStatus.CREATED
                || status == EOrderDetailStatus.ASSIGNED
                || status == EOrderDetailStatus.UNHANDLED
                /*|| status == EOrderDetailStatus.RESCHEDULED*/
                || status == EOrderDetailStatus.UNKNOWN
                ) {
            return DateUtil.isExpiredTime(orderBucket.getServiceTimestamp(), 30) > 0;

        } else
            return false;
    }


    public static void updateOrderStatus(String orderId, String userId, EOrderDetailStatus status, final ListenerModifyData listener) {
        Map<String, Object> keyVal = new HashMap<>();
        keyVal.put("statusDetailId", status.name());

        if (status == EOrderDetailStatus.CANCELLED_BY_TIMEOUT) {
            keyVal.put("statusId", EOrderStatus.FINISHED.name());
        } else {
            keyVal.put("statusId", EOrderStatus.PENDING.name());
        }

        FirebaseDatabase.getInstance().getReference(FBUtil.REF_ORDERS_CUSTOMER_AC_PENDING)
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
    public static boolean isExpiredBooking(OrderHeader order) {
        return isExpiredBooking(order.getTimestamp());
    }

    public static boolean isExpiredBooking(long timestamp) {
        return Util.isExpiredBooking(timestamp, getMobileSetup().getLastOrderMinutes());
    }
*/

    /**
     * Ambil as table master utk service yg akan didaftarkan ke any mitra
     */
    public static void syncServices(final ListenerGetAllData listener) {

        Realm r = Realm.getDefaultInstance();
        try {
            List<SubServiceType> _list = r.copyFromRealm(r.where(SubServiceType.class).findAll());

            // get local cache
            if (_list.size() > 0 && listener != null) {
                listener.onSuccess(_list);
                return;
            }
        } finally {
            r.close();
        }

        FirebaseDatabase.getInstance().getReference(FBUtil.REF_MASTER_AC_SERVICE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Realm _r = Realm.getDefaultInstance();
                        try {
                            _r.beginTransaction();

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                SubServiceType obj = postSnapshot.getValue(SubServiceType.class);

                                _r.copyToRealmOrUpdate(obj);
                            }

                            _r.commitTransaction();

                            if (listener != null) {

                                List<SubServiceType> _list = _r.copyFromRealm(_r.where(SubServiceType.class).findAll());
                                listener.onSuccess(_list);
                            }

                        } finally {
                            _r.close();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        listener.onError(databaseError.toException());
                    }
                });

    }

    public static void syncTechnicianReg() {
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
                        try {
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(list);
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
    }

    public static void syncUserInformation(final Context ctx) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

//        final android.app.AlertDialog alertDialog = Util.showProgressDialog(ctx, "Sync User Info");

        // get user info
        //        https://firebase.googleblog.com/2016/10/become-a-firebase-taskmaster-part-4.html

        // 1
        final TaskCompletionSource<DataSnapshot> getBasicInfo = new TaskCompletionSource<>();
        database.getReference(FBUtil.REF_MITRA_AC).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("basicInfo")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

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

                        getBasicInfo.setResult(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        getBasicInfo.setException(databaseError.toException());
                    }
                });

        // 2
        final TaskCompletionSource<DataSnapshot> getAddress = new TaskCompletionSource<>();
        database.getReference(FBUtil.REF_MITRA_AC).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final List<UserAddress> list = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            list.add(postSnapshot.getValue(UserAddress.class));
                        }

                        Realm r = Realm.getDefaultInstance();
                        try {
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(list);
                                }
                            });
                        } finally {
                            r.close();
                        }
                        getAddress.setResult(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        getAddress.setException(databaseError.toException());
                    }
                });

        // 3
        final TaskCompletionSource<DataSnapshot> getTokens = new TaskCompletionSource<>();
        database.getReference(FBUtil.REF_MITRA_AC).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("firebaseToken")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final List<FirebaseToken> list = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String ss = postSnapshot.getValue(String.class);
                            FirebaseToken ft = new FirebaseToken();
                            ft.setToken(ss);
                            list.add(ft);
                        }
                        Realm r = Realm.getDefaultInstance();
                        try {
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(list);
                                }
                            });
                        } finally {
                            r.close();
                        }

                        getTokens.setResult(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        getTokens.setException(databaseError.toException());
                    }
                });

        //TODO: sync jobs_assigned & jobs_history

        Task<Void> allTask = Tasks.whenAll(getBasicInfo.getTask(), getAddress.getTask(), getTokens.getTask());
        allTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                alertDialog.dismiss();

                if (task.isSuccessful()) {
//                    Toast.makeText(ctx, "Sync All success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static List<TechnicianReg> getAllTechnicianRegByScoring(long bookingTimestamp) {
        List<TechnicianReg> techList = getAllTechnicianReg(true);

        List<TechnicianReg> priorityList = new ArrayList<>();

        Realm r = Realm.getDefaultInstance();
        try{
            MobileSetup mSetup = r.where(MobileSetup.class).findFirst();

            String waktuHariBooking = DateUtil.displayTimeInJakarta(bookingTimestamp, "yyyyMMdd");

            for (TechnicianReg tech : techList) {
                //1. cek max order
                long _count = r.where(JobsAssigned.class)
                        .equalTo("techId", tech.getTechId())
                        .like("wkt", waktuHariBooking + "*")
                        .count();

                if (_count > mSetup.getMaxOrderPerTechnician()) {
                    continue;
                }

                //2. nearby ?

                priorityList.add(tech);
            }
        } finally {
            r.close();
        }

        return priorityList;
    }

    public static void CheckVersions(final String versionName, final ListenerModifyData listener) {
        FirebaseDatabase.getInstance().getReference(REF_MASTER_SETUP)
                .child("versions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (listener == null)
                            return;

                        if (!dataSnapshot.exists())
                            listener.onSuccess();

                        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                        };

                        List<String> versions = dataSnapshot.getValue(t);

                        for (String s : versions) {
                            if (s.equalsIgnoreCase(versionName)) {
                                listener.onSuccess();
                                return;
                            }
                        }

                        listener.onError(new RuntimeException("No Version match"));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // failed to check ? skip
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }
                });
    }

}
