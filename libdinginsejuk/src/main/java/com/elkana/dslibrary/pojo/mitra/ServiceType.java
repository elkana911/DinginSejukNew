package com.elkana.dslibrary.pojo.mitra;

import android.content.Context;

import com.elkana.dslibrary.util.Lang;
import com.elkana.dslibrary.util.Storage;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 20-Oct-17.
 * Dibedakan dgn ServiceItem krn ServiceItem utk teknisi catat charge service.
 * Kalo ServiceType dipakai oleh Mitra utk informasi apa saja service yg tersedia
 */
public class ServiceType extends RealmObject implements Serializable{
    @PrimaryKey
    private int serviceTypeId;
    private String serviceTypeName;
    private String serviceTypeNameBahasa;
    private String promoCode;
    private double rate;

    private boolean enable;
    private boolean visible;
    private long updatedTimestamp;


    public ServiceType() {
        this.enable = true;
        this.visible = true;
    }

    public ServiceType(int serviceTypeId, String serviceTypeName, String serviceTypeNameBahasa) {
        this.serviceTypeId = serviceTypeId;
        this.serviceTypeName = serviceTypeName;
        this.serviceTypeNameBahasa = serviceTypeNameBahasa;
        this.enable = true;
        this.visible = true;
    }

    public static String getLabel(Context context, ServiceType item) {
        if (Storage.getLanguage(context) == Lang.INDONESIAN) {
            return item.getServiceTypeNameBahasa();
        }else
            return item.getServiceTypeName();
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getServiceTypeNameBahasa() {
        return serviceTypeNameBahasa;
    }

    public void setServiceTypeNameBahasa(String serviceTypeNameBahasa) {
        this.serviceTypeNameBahasa = serviceTypeNameBahasa;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String toString() {
        return "ServiceType{" +
                "serviceTypeId=" + serviceTypeId +
                ", serviceTypeName='" + serviceTypeName + '\'' +
                ", serviceTypeNameBahasa='" + serviceTypeNameBahasa + '\'' +
                ", promoCode='" + promoCode + '\'' +
                ", rate=" + rate +
                ", enable=" + enable +
                ", visible=" + visible +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}
