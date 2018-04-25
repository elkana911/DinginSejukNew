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

    // menit yg diperlukan utk last order. harusnya sama utk semua aplikasi.
    // tp ga jelas jg penamaannya, apa maksudnya dihitung dari order pertama seblum masuk order kedua
    // atau maksudnya order buat expiration ? see timeout_cancel_minute
//    private int lastOrderMinutes;

    // utk menangani status UNHANDLED yg tidak ditanggapi oleh mitra. default 60 minutes
    private int status_unhandled_minutes;

    // utk membuang order card dari daftar list utk yg statusnya FINISHED
    private int remove_order_age_hours;
    /**
     * time for an order will cancel automatically after no respond
     * @see #auto_cancel
     */
    private int life_per_status_minute;

    /**
     * hanya boleh booking 2 jam dari waktu sekarang (misalnya)
     */
    private int minimal_booking_hour;

    /**
     * buat jaga2 di masa depan kalo mau ada timer di customer
     */
    private boolean show_timer;

    /**
     * 25 apr 2018
     * fitur baru utk opsi alternatif project, apakah utk order yg serviceTimeFree=true waktu utk teknisi / mitra pilih jamnya secepatnya atau menyusul
     * see orderheader.serviceTimeFree
     */
    private int serviceTimeFreeDecisionType;    // 0: jam harus ditentukan sebeum ambil order, 1: jam dapat ditentukan nanti/menyusul

    /**
     * rencanany tiap status sebelum WORKING bisa auto_cancel tiap life_per_status_minute
     * @see #life_per_status_minute
     */
    private boolean auto_cancel;

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

    public int getLife_per_status_minute() {
        return life_per_status_minute;
    }

    public void setLife_per_status_minute(int life_per_status_minute) {
        this.life_per_status_minute = life_per_status_minute;
    }

    public boolean isMap_show_vendor_title() {
        return map_show_vendor_title;
    }

    public void setMap_show_vendor_title(boolean map_show_vendor_title) {
        this.map_show_vendor_title = map_show_vendor_title;
    }

//    public int getLastOrderMinutes() {
//        return lastOrderMinutes;
//    }

//    public void setLastOrderMinutes(int lastOrderMinutes) {
//        this.lastOrderMinutes = lastOrderMinutes;
//    }

    public long getAppLifeTime() {
        return appLifeTime;
    }

    public int getMax_new_order() {
        return max_new_order;
    }

    public void setMax_new_order(int max_new_order) {
        this.max_new_order = max_new_order;
    }

    public int getStatus_unhandled_minutes() {
        return status_unhandled_minutes;
    }

    public void setStatus_unhandled_minutes(int status_unhandled_minutes) {
        this.status_unhandled_minutes = status_unhandled_minutes;
    }

    public int getRemove_order_age_hours() {
        return remove_order_age_hours;
    }

    public void setRemove_order_age_hours(int remove_order_age_hours) {
        this.remove_order_age_hours = remove_order_age_hours;
    }

    public int getMinimal_booking_hour() {
        return minimal_booking_hour;
    }

    public void setMinimal_booking_hour(int minimal_booking_hour) {
        this.minimal_booking_hour = minimal_booking_hour;
    }

    public boolean isShow_timer() {
        return show_timer;
    }

    public void setShow_timer(boolean show_timer) {
        this.show_timer = show_timer;
    }

    public boolean isAuto_cancel() {
        return auto_cancel;
    }

    public void setAuto_cancel(boolean auto_cancel) {
        this.auto_cancel = auto_cancel;
    }

    public int getServiceTimeFreeDecisionType() {
        return serviceTimeFreeDecisionType;
    }

    public void setServiceTimeFreeDecisionType(int serviceTimeFreeDecisionType) {
        this.serviceTimeFreeDecisionType = serviceTimeFreeDecisionType;
    }

    public void setAppLifeTime(long appLifeTime) {
        this.appLifeTime = appLifeTime;
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
                ", status_unhandled_minutes=" + status_unhandled_minutes +
                ", remove_order_age_hours=" + remove_order_age_hours +
                ", life_per_status_minute=" + life_per_status_minute +
                ", minimal_booking_hour=" + minimal_booking_hour +
                ", show_timer=" + show_timer +
                ", serviceTimeFreeDecisionType=" + serviceTimeFreeDecisionType +
                ", auto_cancel=" + auto_cancel +
                '}';
    }
}
