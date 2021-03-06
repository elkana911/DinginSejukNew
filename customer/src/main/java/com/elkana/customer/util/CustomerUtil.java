package com.elkana.customer.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.customer.R;
import com.elkana.customer.pojo.MobileSetup;
import com.elkana.customer.pojo.QuickOrderProfile;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerGetString;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerSync;
import com.elkana.dslibrary.pojo.Banner;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.mitra.PriceInfo;
import com.elkana.dslibrary.pojo.mitra.TmpMitra;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.NetUtil;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;

/**
 * Created by Eric on 23-Oct-17.
 */

public class CustomerUtil {
    public static final String TAG = CustomerUtil.class.getSimpleName();
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
/* dangerous
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
                    mobileSetup.setLife_per_status_minute(60);
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
    */

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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FBUtil.REF_MITRA_AC);

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
                                } else if (_sub.getKey().equals("info_customer")) {

                                    for (DataSnapshot __sub : _sub.getChildren()) {
                                        PriceInfo pi = __sub.getValue(PriceInfo.class);
                                        r.beginTransaction();
                                        r.copyToRealmOrUpdate(pi);
                                        r.commitTransaction();
                                    }
                                    /*
                                    _sub.getValue(PriceInfo.class);

                                    PriceInfo pi = _sub.getValue(PriceInfo.class);
                                    pi.setMitraId(postSnapshot.getKey());   //fill with mitraid for offline reference

                                    r.beginTransaction();
                                    r.copyToRealmOrUpdate(pi);
                                    r.commitTransaction();
                                    */
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
        FirebaseDatabase.getInstance().getReference(FBUtil.REF_ORDERS_MITRA_AC_PENDING)
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

        FirebaseDatabase.getInstance().getReference(FBUtil.REF_ORDERS_CUSTOMER_AC_PENDING)
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

    public static void syncUserInformation(final Context ctx) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (currentUser == null) {
            Log.e(TAG, "Please login to synchronize User Information");
            return;
        }

//        final android.app.AlertDialog alertDialog = Util.showProgressDialog(ctx, "Sync User Info");
//        https://firebase.googleblog.com/2016/10/become-a-firebase-taskmaster-part-4.html

        // 2
        final TaskCompletionSource<DataSnapshot> getAddress = new TaskCompletionSource<>();
        database.getReference("users/" + currentUser.getUid()).child("address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

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
                        getAddress.setResult(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        getAddress.setException(databaseError.toException());
                    }
                });

        // 3
        final TaskCompletionSource<DataSnapshot> getBasicInfo = new TaskCompletionSource<>();
        database.getReference("users/" + currentUser.getUid()).child("basicInfo")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final BasicInfo _obj = dataSnapshot.getValue(BasicInfo.class);

                        Realm r = Realm.getDefaultInstance();
                        try {
                            r.beginTransaction();
                            r.copyToRealmOrUpdate(_obj);
                            r.commitTransaction();

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

        Task<Void> allTask = Tasks.whenAll(getBasicInfo.getTask(), getAddress.getTask());
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

        /* unless you want this to put them on asynctask
        try {
            // Block on the task for a maximum of 500 milliseconds, otherwise time out.
            Tasks.await(allTask, 1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        System.out.println("TRAP");
        if (true)
            return;
            */
/*

        // syncsetup
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

        //tokens ga boleh menyimpan token dari device lain. token secara default diperoleh di MyFirebaseInstanceIDService
*/
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
*/

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

    /**
     * @param ctx
     * @param nextDayYYYYMMDD jika nextDayYYYYMMDD adalah hari ini, maka jam tidak akan menampilkan jam yg telah lewat.
     * @param offsetHour jika 2, maka waktu open hour akan digeser 2 jam
     * @param mitraName
     * @param listener
     */
    public static void showDialogTimeOfService(Context ctx, String nextDayYYYYMMDD, int offsetHour, String mitraName, final ListenerGetString listener) {

        // filter waktu buka berdasarkan hari service, dalam hal ini nextDayYYYYMMDD
        int openTime, closeTime;
        Realm _realm = Realm.getDefaultInstance();
        try {
            Mitra mitraObj = CustomerUtil.lookUpMitra(_realm, mitraName);

            openTime = mitraObj.getWorkingHourStart();
            closeTime = mitraObj.getWorkingHourEnd();

        }finally {
            _realm.close();
        }

        Date now = new Date();
        String today = Util.convertDateToString(now, "yyyyMMdd");
        if (nextDayYYYYMMDD.equals(today)) {
            Calendar c = Calendar.getInstance();
            c.setTime(now);

            int currentHour = c.get(Calendar.HOUR_OF_DAY);
            if (currentHour > openTime)
                openTime = currentHour + offsetHour;
        }

        final String[] time_services = DateUtil.generateWorkingHours(openTime, closeTime, 15);

        if (time_services == null) {
            Toast.makeText(ctx, "Sudah diluar jam kerja. Silakan ganti hari.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Pilih Jam");

//        https://android--examples.blogspot.co.id/2016/10/android-alertdialog-listadapter-example.html
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(ctx, android.R.layout.simple_list_item_single_choice, time_services){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                if (position % 2 == 0) { // we're on an even row
//                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardColor));
//                } else {
//                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardColor));
//                }
//                return view;
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = inflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);
                if (position % 2 == 0) { // we're on an even row
                    row.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPaperBack));
                } else {
                    row.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                }
                TextView t1 = row.findViewById(android.R.id.text1);
                t1.setText(time_services[position]);

                return row;
            }
/*
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                TextView t1 = row.findViewById(android.R.id.text1);
                t1.setText(time_services[position] + "XXXX");

                return row;
            }
*/
        };

//        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });

        builder.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener == null)
                    return;

                if (which > -1) {
                    String pick = time_services[which];

                    listener.onSuccess(pick);


                }

            }
        });

        builder.setPositiveButton("OK", null);

        //        builder.setItems(time_services, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                if (listener == null)
//                    return;
//                String pick = time_services[which];
//
//                listener.onSuccess(pick);
//            }
//        });

        AlertDialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_dark);

        dialog.show();

    }


//    public static boolean isExpiredBooking(OrderHeader order) {
//        return Util.isExpiredBooking(order.getTimestamp(), getMobileSetup().getLastOrderMinutes());
//    }

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

    public static void GetBanners(final ListenerGetAllData listener) {

        FirebaseDatabase.getInstance().getReference(REF_MASTER_SETUP)
                .child("banner")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (listener == null)
                            return;

                        GenericTypeIndicator<ArrayList<Banner>> t = new GenericTypeIndicator<ArrayList<Banner>>() {
                        };

                        List<Banner> banners = dataSnapshot.getValue(t);

                        listener.onSuccess(banners);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // failed to check ? skip
                        if (listener != null) {
                            listener.onError(databaseError.toException());
                        }

                    }
                });

    }

    public static void SaveOrderProfile(String userId, String profileLabel, QuickOrderProfile profile, ListenerModifyData listener) {

        if (profile == null || Util.isEmpty(profileLabel)) {
            if (listener != null)
                listener.onError(new RuntimeException("mandatory profile data"));
        }

        FBUtil.Customer_GetRef(userId)
                .child("orderProfile")
                .child(String.valueOf(profile.getUid()))
                .setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {

                }
            }
        });

    }
}
