package com.elkana.dslibrary.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 01-Dec-17.
 */
//orders/ac/pending/mitra/<mitraId>
public class OrderBucket extends RealmObject implements Serializable {
    @PrimaryKey
    private String uid; // order id
    private String customerId;
    private String customerName;
    private String addressByGoogle;
    private String partyId;
    private String technicianId;
    private String technicianName;
    private String statusDetailId;
    private String statusComment;   // diperlukan jika Server cancel dan customer diberikan alasan mengapa di cancel
    private int acCount;
    private int minuteExtra;    //buat timer. diisi dari server
    private long bookingTimestamp;
    private long updatedTimestamp;  // the time when server received order. bisa dipakai utk start timeout
    private String updatedBy;

    public OrderBucket() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddressByGoogle() {
        return addressByGoogle;
    }

    public void setAddressByGoogle(String addressByGoogle) {
        this.addressByGoogle = addressByGoogle;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getStatusDetailId() {
        return statusDetailId;
    }

    public void setStatusDetailId(String statusDetailId) {
        this.statusDetailId = statusDetailId;
    }

    public String getStatusComment() {
        return statusComment;
    }

    public void setStatusComment(String statusComment) {
        this.statusComment = statusComment;
    }

    public long getBookingTimestamp() {
        return bookingTimestamp;
    }

    public void setBookingTimestamp(long bookingTimestamp) {
        this.bookingTimestamp = bookingTimestamp;
    }

    public int getAcCount() {
        return acCount;
    }

    public void setAcCount(int acCount) {
        this.acCount = acCount;
    }

    public int getMinuteExtra() {
        return minuteExtra;
    }

    public void setMinuteExtra(int minuteExtra) {
        this.minuteExtra = minuteExtra;
    }

    public long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "OrderBucket{" +
                "uid='" + uid + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", addressByGoogle='" + addressByGoogle + '\'' +
                ", partyId='" + partyId + '\'' +
                ", technicianId='" + technicianId + '\'' +
                ", technicianName='" + technicianName + '\'' +
                ", statusDetailId='" + statusDetailId + '\'' +
                ", statusComment='" + statusComment + '\'' +
                ", acCount=" + acCount +
                ", minuteExtra=" + minuteExtra +
                ", bookingTimestamp=" + bookingTimestamp +
                ", updatedTimestamp=" + updatedTimestamp +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
