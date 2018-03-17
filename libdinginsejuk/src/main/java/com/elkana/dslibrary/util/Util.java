package com.elkana.dslibrary.util;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;


import com.elkana.dslibrary.BuildConfig;
import com.elkana.dslibrary.R;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;

/**
 * Created by Eric on 19-Oct-17.
 */

public class Util {

    public static final boolean DEVELOPER_MODE = true;
    public static final boolean TESTING_MODE = true;
    public static final String TESTING_USER = "4AAwmPGueYNiKhJuw2rFlDEYAqD2";

    /**
     * @param ctx
     * @param resourceId R.drawable.ic_mail_outline_black_24dp
     * @param colorId    android.R.color.white
     * @return
     */
    public static Drawable changeIconColor(Context ctx, int resourceId, int colorId) {
        Drawable icon;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            icon = VectorDrawableCompat.create(ctx.getResources(), resourceId, ctx.getTheme());
        } else {
            icon = ctx.getResources().getDrawable(resourceId, ctx.getTheme());
        }

        icon = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(icon, ContextCompat.getColor(ctx, colorId));
        return icon;
    }

    public static Drawable changeIconColor(Context ctx, Drawable icon, int colorId) {
        icon = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(icon, ContextCompat.getColor(ctx, colorId));
        return icon;
    }

    public static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && email.contains("@") && email.length() > 4;
    }

    public static String prettyTimestamp(Context ctx, long timestamp) {
        Date date = new Date(timestamp);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        StringBuffer waktu = new StringBuffer();
        waktu.append(Util.prettyDate(ctx, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR), true))
                .append(" ").append(Util.convertDateToString(date, "HH:mm"));

        return waktu.toString();

    }

    public static String prettyDate(Context ctx, Date date, boolean showDetail) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return prettyDate(ctx, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR), showDetail);
    }

    public static String prettyDate(Context ctx, int dayOfMonth, int monthOfYear0, int year, boolean showDetail) {
        // param range based on
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear0, dayOfMonth);


        String tgl = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(c.getTime());

        Calendar today = Calendar.getInstance();
        int mYear = today.get(Calendar.YEAR);
        int mMonth = today.get(Calendar.MONTH);
        int mDay = today.get(Calendar.DAY_OF_MONTH);

        if (dayOfMonth == mDay && monthOfYear0 == mMonth && year == mYear) {
            return !showDetail ? ctx.getString(R.string.label_today) : ctx.getString(R.string.label_today) + ", " + tgl;
        }

        if (dayOfMonth == (mDay + 1) && monthOfYear0 == mMonth && year == mYear) {
            return !showDetail ? ctx.getString(R.string.label_tomorrow) : ctx.getString(R.string.label_tomorrow) + ", " + tgl;
        }

        String hari = "Hari ini";
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                hari = "Minggu";
                break;
            case 2:
                hari = "Senin";
                break;
            case 3:
                hari = "Selasa";
                break;
            case 4:
                hari = "Rabu";
                break;
            case 5:
                hari = "Kamis";
                break;
            case 6:
                hari = "Jumat";
                break;
            case 7:
                hari = "Sabtu";
        }

        return hari + ", " + tgl;
//        return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(c.getTime());
//        return "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
    }


    public static String convertDateToString(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date convertStringToDate(String date, String pattern) {
//        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat sdf1 = new SimpleDateFormat(pattern);
        try {
            return sdf1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getDeviceResolution(Context ctx) {
        int density = ctx.getResources().getDisplayMetrics().densityDpi;
        switch (density) {
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tv";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            default:
                return "Unknown";

        }

    }

    public static ProgressDialog createAndShowProgressDialog(Context ctx, String msg) {
        ProgressDialog mProgressDialog = new ProgressDialog(ctx);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(msg);

        mProgressDialog.show();

        return mProgressDialog;
    }

    public static void dismissDialog(ProgressDialog dialog) {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    public static android.app.AlertDialog showProgressDialog(Context ctx, String specific_msg) {
        android.app.AlertDialog dialog = new SpotsDialog(ctx, Util.DEVELOPER_MODE ? specific_msg : ctx.getString(R.string.message_please_wait));

        dialog.show();
        
        return dialog;
    }

    public static android.app.AlertDialog showProgressDialog(Context ctx) {
        android.app.AlertDialog dialog = new SpotsDialog(ctx, ctx.getString(R.string.message_please_wait));

        dialog.show();

        return dialog;
    }

    public static void showDialog(Context ctx, String title, String message) {
        showDialog(ctx, title, message, false);
    }

    public static void showErrorDialog(Context ctx, String title, String message) {
        showDialog(ctx, title, message, true);
    }

    public static void showDialog(Context ctx, String title, String message, boolean asError) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
//        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();

    }

    public static void showDialogConfirmation(Context ctx, String title, String message, final ListenerPositiveConfirmation listener) {
        /*
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
//        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null)
                    listener.onPositive();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
*/
        showDialogConfirmation(ctx, title, message, null, listener);
    }

    public static void showDialogConfirmation(Context ctx, String title, String message, View view, final ListenerPositiveConfirmation listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

        if (view != null)
            alertDialogBuilder.setView(view);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null)
                    listener.onPositive();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();

    }

    public static String buildSysInfoAsCsv(Context ctx) {
        //date, version app, version os, imei, is location enabled etc in csv format
        StringBuffer sb = new StringBuffer("date=" + convertDateToString(new Date(), "yyyyMMddHHmmss"));

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        sb.append(",").append("dev=").append(DEVELOPER_MODE);
        sb.append(",").append("versionCode=").append(versionCode);
        sb.append(",").append("versionName=").append(versionName);
        sb.append(",").append("versionAPI=").append(Build.VERSION.SDK_INT);

        try {
            if (ctx != null) {
                sb.append(",").append("server=Firebase");

                // single sim non dual
                TelephonyManager mngr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                } else {
                    sb.append(",").append("imei=").append(mngr.getDeviceId());
                    sb.append(",").append("simSN=").append(mngr.getSimSerialNumber());
                }

                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                String serial1 = (String) get.invoke(c, "ril.serialnumber");
                String id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
                sb.append(",").append("deviceSN=").append(TextUtils.isEmpty(serial1) ? Build.SERIAL : serial1);
                sb.append(",").append("androidId=").append(id);

                LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                }

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                }

                sb.append(",").append("gpsEnabled=").append(gps_enabled);
                sb.append(",").append("networkEnabled=").append(network_enabled);

                sb.append(",").append("language=").append(Storage.getLanguageId(ctx));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static String convertLongToRupiah(long amount) {
//        double harga = 250000000;

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setMinimumFractionDigits(0);  // sen minta diilangin

        kursIndonesia.setDecimalFormatSymbols(formatRp);
//        System.out.printf("Harga Rupiah: %s %n", kursIndonesia.format(amount));    //Harga Rupiah: Rp. 250.000.000,00

        return kursIndonesia.format(amount);
    }


/*
    public static boolean isOrderExpired(OrderHeader orderHeader) {
        return EOrderStatus.convertValue(orderHeader.getStatusId()) != EOrderStatus.FINISHED
                && (EOrderDetailStatus.convertValue(orderHeader.getStatusDetailId()) != EOrderDetailStatus.ASSIGNED)
                && DateUtil.isBeforeDay(new Date(orderHeader.getTimestamp()), new Date());
    }
*/

    /**
     * 1 hour(60 minutes) from now
     *
     * @param timeMillisToCheck
     * @param lastMinutes if 30, 30 minutes from now is expired
     * @return
     */
    public static boolean isExpiredTime(long timeMillisToCheck, int lastMinutes) {
        Date date = new Date(timeMillisToCheck);
        // 2017-12-13 07:22

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, lastMinutes);
        // 2017-12-13  07:22 + minuteToleransi

        if (date.getTime() < c.getTimeInMillis()) {
            return true;
        } else {
            return false;
        }

    }

    public static String counter(String numeric, boolean add, int minValue, int maxValue) {
        int x = Integer.parseInt(numeric) + (add ? 1 : -1);

        if (x < minValue)
            x = minValue;

        if (maxValue > -1)
            if (x > maxValue)
                x = maxValue;


        return String.valueOf(x);
    }

    public static File createTempFileForCamera(Context ctx) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static String joinStrings(String[] list, String delimiter){
        return TextUtils.join(delimiter, list);
    }

    public static boolean isPhoneValid(String phone) {
        return !TextUtils.isEmpty(phone) && phone.length() > 4;
    }

    // fungsi yg cukup bahaya. jgn dipake dulu. harusnya expiredtime bisa diprediksi, jgn 0
    @Deprecated
    public static boolean isExpiredOrder(OrderHeader orderHeader) {
        EOrderDetailStatus status = EOrderDetailStatus.convertValue(orderHeader.getStatusDetailId());
        if (status == EOrderDetailStatus.CREATED
                || status == EOrderDetailStatus.ASSIGNED
                || status == EOrderDetailStatus.UNHANDLED
                /*|| status == EOrderDetailStatus.RESCHEDULED*/
                || status == EOrderDetailStatus.UNKNOWN
                ) {
            return isExpiredTime(orderHeader.getTimestamp(), 30);   //hardcode 30

        } else
            return false;
    }

    // fungsi yg cukup bahaya. jgn dipake dulu
    @Deprecated
    public static boolean isExpiredOrder(OrderBucket orderBucket) {
        EOrderDetailStatus status = EOrderDetailStatus.convertValue(orderBucket.getStatusDetailId());

        if (status == EOrderDetailStatus.CREATED
                || status == EOrderDetailStatus.ASSIGNED
                || status == EOrderDetailStatus.UNHANDLED
                /*|| status == EOrderDetailStatus.RESCHEDULED*/
                || status == EOrderDetailStatus.UNKNOWN
                ) {
            return isExpiredTime(orderBucket.getOrderTimestamp(), 30);

        } else
            return false;
    }

    public static Map getFieldNamesAndValues(final Object valueObj) throws IllegalArgumentException,
            IllegalAccessException
    {
        System.out.println("Begin - getFieldNamesAndValues");
        Class c1 = valueObj.getClass();
        System.out.println("Class name got is:: " + c1.getName());

        Map fieldMap = new HashMap();
        Field[] valueObjFields = c1.getDeclaredFields();

        // compare values now
        for (int i = 0; i < valueObjFields.length; i++)
        {

            String fieldName = valueObjFields[i].getName();

            System.out.println("Getting Field Values for Field:: " + valueObjFields[i].getName());
            valueObjFields[i].setAccessible(true);

            Object newObj = valueObjFields[i].get(valueObj);

            Class t = valueObjFields[i].getType();

            if (t.equals(String.class)){
                System.out.println("STRING!!!");
            }

            //if(f.getType().equals(int.class))

            //if(f.getType().equals(long.class))

//            if(f.getType().equals(List.class)){
//                result.add(f.getName());
//            }

            //for other data type

            //Map
            //if(f.getType().equals(Map.class))

            System.out.println("Value of field" + fieldName + "newObj:: " + newObj);
            fieldMap.put(fieldName, newObj);

        }
        System.out.println("End - getFieldNamesAndValues");
        return fieldMap;
    }

    public static String removeTrailingSlash(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }
}
