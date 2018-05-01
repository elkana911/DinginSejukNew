package com.elkana.dslibrary.util;

/**
 * Created by Eric on 19-Oct-17.
 */

public class Const {
    public static final String ARG_FIREBASE_TOKEN = "firebaseToken";
    public static final int USER_AS_COSTUMER = 10;
    public static final int USER_AS_MITRA = 20;
    public static final int USER_AS_TECHNICIAN = 30;

    public static final int CONFIG_AS_COSTUMER = 1;
    public static final int CONFIG_AS_MITRA = 2;
    public static final int CONFIG_AS_TECHNICIAN = 3;

    public static final int SERVICE_TYPE_TAX_CHARGE = -4;
    public static final int SERVICE_TYPE_QUICK_SERVICE_CHARGE = -5;
    public static final String SERVICE_TYPE_QUICK_SERVICE_CHARGE_LABEL = "Layanan Segera";
    public static final int SERVICE_TYPE_DISCOUNT = -6;

    public static final int SERVICE_TYPE_SCHEDULED = 2;
    public static final int SERVICE_TYPE_QUICK = 1;


    // id to handle the notification in the notification tray
    public static final String PUSH_NOTIFICATION = "pushNotification";
//    public static final int NOTIFICATION_ID = 100;
//    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String KEY_FROM = "key_from";
    public static final String KEY_UID = "key_uid";
    public static final String KEY_STATUS = "key_status";
    //    public static final String KEY_SEQNO = "key_seqno";
    public static final String KEY_BOOKING_TIMESTAMP = "key_timestamp";
    public static final String KEY_MESSAGE = "key_message";

    public static final int SERVICETIMEFREEDECISIONTYPE_LATER = 0;
    public static final int SERVICETIMEFREEDECISIONTYPE_NOW = 1;
}
