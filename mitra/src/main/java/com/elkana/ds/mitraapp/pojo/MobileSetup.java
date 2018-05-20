package com.elkana.ds.mitraapp.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 02-Nov-17.
 */

public class MobileSetup extends RealmObject implements Serializable {
    @PrimaryKey
    private int id;

    private int maxInfoPriceChars;

    private boolean gps_mandatory;
    private long appLifeTime;
    private String theme_color_default;
    private String theme_color_default_inactive;
    private String theme_color_default_accent;

//    private int lastOrderMinutes;   // menit yg diperlukan utk last order. harusnya sama utk semua aplikasi
    private boolean singleInstance; // true to avoid application logged in on multiple device
    private int maxOrderPerTechnician;

    /**
     * time for an order will cancel automatically after no respond
     */
    private int timeout_cancel_minute;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxInfoPriceChars() {
        return maxInfoPriceChars;
    }

    public void setMaxInfoPriceChars(int maxInfoPriceChars) {
        this.maxInfoPriceChars = maxInfoPriceChars;
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

//    public int getLastOrderMinutes() {
//        return lastOrderMinutes;
//    }
//
//    public void setLastOrderMinutes(int lastOrderMinutes) {
//        this.lastOrderMinutes = lastOrderMinutes;
//    }

    public int getTimeout_cancel_minute() {
        return timeout_cancel_minute;
    }

    public void setTimeout_cancel_minute(int timeout_cancel_minute) {
        this.timeout_cancel_minute = timeout_cancel_minute;
    }

    public boolean isSingleInstance() {
        return singleInstance;
    }

    public void setSingleInstance(boolean singleInstance) {
        this.singleInstance = singleInstance;
    }

    public int getMaxOrderPerTechnician() {
        return maxOrderPerTechnician;
    }

    public void setMaxOrderPerTechnician(int maxOrderPerTechnician) {
        this.maxOrderPerTechnician = maxOrderPerTechnician;
    }

    @Override
    public String toString() {
        return "MobileSetup{" +
                "id=" + id +
                ", maxInfoPriceChars=" + maxInfoPriceChars +
                ", gps_mandatory=" + gps_mandatory +
                ", appLifeTime=" + appLifeTime +
                ", theme_color_default='" + theme_color_default + '\'' +
                ", theme_color_default_inactive='" + theme_color_default_inactive + '\'' +
                ", theme_color_default_accent='" + theme_color_default_accent + '\'' +
                ", singleInstance=" + singleInstance +
                ", maxOrderPerTechnician=" + maxOrderPerTechnician +
                ", timeout_cancel_minute=" + timeout_cancel_minute +
                '}';
    }
}
