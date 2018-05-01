package com.elkana.teknisi.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 02-Nov-17.
 */

public class MobileSetup extends RealmObject implements Serializable {
    @PrimaryKey
    private int id;

    private boolean gps_mandatory;
    private long appLifeTime;
    private String theme_color_default;
    private String theme_color_default_inactive;
    private String theme_color_default_accent;
//    private int window_new_order_minutes;    //menit yg diperlukan utk timer kasih waktu buat order baru. pd prakteknya akan dikurangi 1 menit.

    private boolean trackingGps;
    private String trackingOrderId;
    private int min_minutes_otw;    //minimal teknisi baru boleh jalan (satuan menit)
    /**
     * time for an order will cancel automatically after no respond
     */
    private int timeout_cancel_minute;  // ga jelas maksudnya apa sih

    private boolean reminderToOtw;      // able to install reminder when status is ASSIGNED

    private int movements_gap_minutes;  // 10 menit

    public MobileSetup() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isGps_mandatory() {
        return gps_mandatory;
    }

    public void setGps_mandatory(boolean gps_mandatory) {
        this.gps_mandatory = gps_mandatory;
    }

    public long getAppLifeTime() {
        return appLifeTime;
    }

    public void setAppLifeTime(long appLifeTime) {
        this.appLifeTime = appLifeTime;
    }

    public String getTheme_color_default() {
        return theme_color_default;
    }

    public void setTheme_color_default(String theme_color_default) {
        this.theme_color_default = theme_color_default;
    }

    public String getTheme_color_default_inactive() {
        return theme_color_default_inactive;
    }

    public void setTheme_color_default_inactive(String theme_color_default_inactive) {
        this.theme_color_default_inactive = theme_color_default_inactive;
    }

    public String getTheme_color_default_accent() {
        return theme_color_default_accent;
    }

    public void setTheme_color_default_accent(String theme_color_default_accent) {
        this.theme_color_default_accent = theme_color_default_accent;
    }

    public boolean isTrackingGps() {
        return trackingGps;
    }

    public void setTrackingGps(boolean trackingGps) {
        this.trackingGps = trackingGps;
    }

    public String getTrackingOrderId() {
        return trackingOrderId;
    }

    public void setTrackingOrderId(String trackingOrderId) {
        this.trackingOrderId = trackingOrderId;
    }

    public int getTimeout_cancel_minute() {
        return timeout_cancel_minute;
    }

    public void setTimeout_cancel_minute(int timeout_cancel_minute) {
        this.timeout_cancel_minute = timeout_cancel_minute;
    }

    public int getMin_minutes_otw() {
        return min_minutes_otw;
    }

    public void setMin_minutes_otw(int min_minutes_otw) {
        this.min_minutes_otw = min_minutes_otw;
    }

//    public int getWindow_new_order_minutes() {
//        return window_new_order_minutes;
//    }

//    public void setWindow_new_order_minutes(int window_new_order_minutes) {
//        this.window_new_order_minutes = window_new_order_minutes;
//    }


    public boolean isReminderToOtw() {
        return reminderToOtw;
    }

    public void setReminderToOtw(boolean reminderToOtw) {
        this.reminderToOtw = reminderToOtw;
    }

    public int getMovements_gap_minutes() {
        return movements_gap_minutes;
    }

    public void setMovements_gap_minutes(int movements_gap_minutes) {
        this.movements_gap_minutes = movements_gap_minutes;
    }

    @Override
    public String toString() {
        return "MobileSetup{" +
                "id=" + id +
                ", gps_mandatory=" + gps_mandatory +
                ", appLifeTime=" + appLifeTime +
                ", theme_color_default='" + theme_color_default + '\'' +
                ", theme_color_default_inactive='" + theme_color_default_inactive + '\'' +
                ", theme_color_default_accent='" + theme_color_default_accent + '\'' +
                ", trackingGps=" + trackingGps +
                ", trackingOrderId='" + trackingOrderId + '\'' +
                ", min_minutes_otw=" + min_minutes_otw +
                ", timeout_cancel_minute=" + timeout_cancel_minute +
                ", reminderToOtw=" + reminderToOtw +
                ", movements_gap_minutes=" + movements_gap_minutes +
                '}';
    }
}
