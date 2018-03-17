package com.elkana.teknisi.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.mitra.ServiceType;
import com.elkana.dslibrary.pojo.mitra.SubServiceType;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.pojo.technician.Technician;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MobileSetup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Eric on 23-Oct-17.
 */

public class DataUtil {
    public static final String TAG = DataUtil.class.getSimpleName();
//    public static final String REF_VENDORS_AC = "master/party/supplierAC";

    public static final String REF_ORDERS_CUSTOMER_AC_PENDING = "orders/ac/pending/customer";
    public static final String REF_ORDERS_MITRA_AC_PENDING = "orders/ac/pending/mitra";
    public static final String REF_ORDERS_AC_FINISHED = "orders/ac/finished";

    //    public static final String REF_ORDERS_AC = "orders/ac/orderHeader";
    public static final String REF_SUBSERVICEAC = "master/serviceType/airConditioner/subService";   // biasanya dipakai di HQ utk available service for all mitra. tp tetep diunduh ke teknisi karena akan dimapping dengan serviceToParty
    public static final String REF_VENDOR_AC_SERVICES = "serviceToParty/ac";

    public static final String REF_MITRA_AC = "mitra/ac";
    public static final String REF_TECHNICIAN_AC = "technicians/ac";

    public static final String REF_ASSIGNMENTS_PENDING = "assignments/ac/pending";
    public static final String REF_ASSIGNMENTS_FINISHED = "assignments/ac/finished";

    public static final String REF_MOVEMENTS= "movements";
    public static final String REF_MASTER_SETUP = "master/mSetup/" + Const.CONFIG_AS_TECHNICIAN;

    public static void getOnlineDataToOffline() {

    }

    public static void cleanTransactionData() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();

            realm.where(ServiceItem.class).findAll().deleteAllFromRealm();

            realm.where(Technician.class).findAll().deleteAllFromRealm();
//            realm.where(Mitra.class).findAll().deleteAllFromRealm();
//            realm.where(OrderDetail.class).findAll().deleteAllFromRealm();
//            realm.where(OrderHeader.class).findAll().deleteAllFromRealm();
//            realm.where(BasicInfo.class).findAll().deleteAllFromRealm();
//            realm.where(FirebaseToken.class).findAll().deleteAllFromRealm();
//            realm.where(UserAddress.class).findAll().deleteAllFromRealm();
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
                    mobileSetup.setId(Const.CONFIG_AS_TECHNICIAN);   // hardcode as Teknisi Setup

                    mobileSetup.setGps_mandatory(true);
                    mobileSetup.setTheme_color_default("#143664");
                    mobileSetup.setTheme_color_default_accent("#00f73e");
                    mobileSetup.setTheme_color_default_inactive("#000000");

                    /*
                    mobileSetup.setTheme_color_cleaning("#00796a");
                    mobileSetup.setTheme_color_cleaning_accent("#e7f4fc");
                    mobileSetup.setTheme_color_cleaning_inactive("#bac9cc");
                    mobileSetup.setTheme_color_electric("#00796a");
                    mobileSetup.setTheme_color_electric_accent("#e7f4fc");
                    mobileSetup.setTheme_color_electric_inactive("#bac9cc");
                    mobileSetup.setTimeout_cancel_minute(Util.TESTING_MODE ? 2 : 30);*/
                    realm.copyToRealmOrUpdate(new ServiceType(10, "Air Conditioner", "Air Conditioner"));
                    realm.copyToRealmOrUpdate(new ServiceType(20, "Electricity", "PLN/Listrik"));

                    realm.copyToRealmOrUpdate(new SubServiceType(10, "Cleaning", "Bersihin AC"));
                    realm.copyToRealmOrUpdate(new SubServiceType(20, "Freon Charge", "Freon Charge"));
                    realm.copyToRealmOrUpdate(new SubServiceType(30, "Installation", "Instalasi"));

                    mobileSetup.setTrackingGps(false);
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
            case PAID:
                return ctx.getString(R.string.status_paid);
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

    public static Mitra lookUpMitra(Realm realm, long mitraId) {
        Mitra mitraObj = realm.where(Mitra.class)
                .equalTo("id", mitraId)
                .findFirst();

        return mitraObj;
    }

    public static Mitra lookUpMitra(Realm realm, String name) {
        Mitra mitraObj = realm.where(Mitra.class)
                .equalTo("name", name)
                .findFirst();

        return mitraObj;
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
}
