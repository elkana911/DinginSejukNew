package com.elkana.customer.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 02-Nov-17.
 */

public class MobileSetup extends RealmObject implements Serializable {
    @PrimaryKey
    private int id;

    private boolean map_show_vendor_title;
    private boolean gps_mandatory;
    private long appLifeTime;
    private String theme_color_default;
    private String theme_color_default_inactive;
    private String theme_color_default_accent;

    private int vendor_radius_km;
    private int max_new_order;  // dibatasin maksimal order baru spy custmer ga nakal create order melulu
    private int unit_ac_max;
    private int lastOrderMinutes;   // menit yg diperlukan utk last order. harusnya sama utk semua aplikasi

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

    public int getVendor_radius_km() {
        return vendor_radius_km;
    }

    public void setVendor_radius_km(int vendor_radius_km) {
        this.vendor_radius_km = vendor_radius_km;
    }

    public int getUnit_ac_max() {
        return unit_ac_max;
    }

    public void setUnit_ac_max(int unit_ac_max) {
        this.unit_ac_max = unit_ac_max;
    }

    public int getTimeout_cancel_minute() {
        return timeout_cancel_minute;
    }

    public void setTimeout_cancel_minute(int timeout_cancel_minute) {
        this.timeout_cancel_minute = timeout_cancel_minute;
    }

    public boolean isMap_show_vendor_title() {
        return map_show_vendor_title;
    }

    public void setMap_show_vendor_title(boolean map_show_vendor_title) {
        this.map_show_vendor_title = map_show_vendor_title;
    }

    public int getLastOrderMinutes() {
        return lastOrderMinutes;
    }

    public void setLastOrderMinutes(int lastOrderMinutes) {
        this.lastOrderMinutes = lastOrderMinutes;
    }

    public long getAppLifeTime() {
        return appLifeTime;
    }

    public int getMax_new_order() {
        return max_new_order;
    }

    public void setMax_new_order(int max_new_order) {
        this.max_new_order = max_new_order;
    }

    @Override
    public String toString() {
        return "MobileSetup{" +
                "id=" + id +
                ", map_show_vendor_title=" + map_show_vendor_title +
                ", gps_mandatory=" + gps_mandatory +
                ", appLifeTime=" + appLifeTime +
                ", theme_color_default='" + theme_color_default + '\'' +
                ", theme_color_default_inactive='" + theme_color_default_inactive + '\'' +
                ", theme_color_default_accent='" + theme_color_default_accent + '\'' +
                ", vendor_radius_km=" + vendor_radius_km +
                ", max_new_order=" + max_new_order +
                ", unit_ac_max=" + unit_ac_max +
                ", lastOrderMinutes=" + lastOrderMinutes +
                ", timeout_cancel_minute=" + timeout_cancel_minute +
                '}';
    }

    public void setAppLifeTime(long appLifeTime) {
        this.appLifeTime = appLifeTime;
    }

}
