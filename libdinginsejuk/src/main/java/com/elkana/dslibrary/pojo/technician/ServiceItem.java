package com.elkana.dslibrary.pojo.technician;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 14-Nov-17.
 */

public class ServiceItem extends RealmObject implements Serializable{
    @PrimaryKey
    private long uid;   //timestamp, because theres no way technician can do insert async
    private String assignmentId;
    private int serviceTypeId;
    private String serviceLabel;
    private String promoCode;
    private int count;
    private double rate;
    private long uidNegative;   // for sorting desc trick

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getServiceLabel() {
        return serviceLabel;
    }

    public void setServiceLabel(String serviceLabel) {
        this.serviceLabel = serviceLabel;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getUidNegative() {
        return uidNegative;
    }

    public void setUidNegative(long uidNegative) {
        this.uidNegative = uidNegative;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    @Override
    public String toString() {
        return "ServiceItem{" +
                "uid=" + uid +
                ", assignmentId='" + assignmentId + '\'' +
                ", serviceTypeId=" + serviceTypeId +
                ", serviceLabel='" + serviceLabel + '\'' +
                ", promoCode='" + promoCode + '\'' +
                ", count=" + count +
                ", rate=" + rate +
                ", uidNegative=" + uidNegative +
                '}';
    }
}
