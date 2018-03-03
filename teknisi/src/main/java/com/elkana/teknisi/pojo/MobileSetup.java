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
    private String theme_color_default;
    private String theme_color_default_inactive;
    private String theme_color_default_accent;

    private boolean trackingGps;
    private String trackingOrderId;
    private int expiryLastOrderMinutes;   // menit yg diperlukan utk last order. harusnya sama utk semua aplikasi
    /**
     * time for an order will cancel automatically after no respond
     */
    private int timeout_cancel_minute;

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

    public int getExpiryLastOrderMinutes() {
        return expiryLastOrderMinutes;
    }

    public void setExpiryLastOrderMinutes(int expiryLastOrderMinutes) {
        this.expiryLastOrderMinutes = expiryLastOrderMinutes;
    }

    @Override
    public String toString() {
        return "MobileSetup{" +
                "id=" + id +
                ", gps_mandatory=" + gps_mandatory +
                ", theme_color_default='" + theme_color_default + '\'' +
                ", theme_color_default_inactive='" + theme_color_default_inactive + '\'' +
                ", theme_color_default_accent='" + theme_color_default_accent + '\'' +
                ", trackingGps=" + trackingGps +
                ", trackingOrderId='" + trackingOrderId + '\'' +
                ", expiryLastOrderMinutes=" + expiryLastOrderMinutes +
                ", timeout_cancel_minute=" + timeout_cancel_minute +
                '}';
    }
}
