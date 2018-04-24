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
    private String invoiceNo;
    private String customerId;
    private String customerName;
    private String addressByGoogle;
    private String partyId;
    private String technicianId;
    private String technicianName;
    private String statusId;    // kepake buat main2 warna yg udah FINISHED
    private String statusDetailId;
    private String statusComment;   // diperlukan jika Server cancel dan customer diberikan alasan mengapa di cancel
    private int acCount;
    private int minuteExtra;    //buat timer. diisi dari server
    private String dateOfService;   // yyyyMMdd. bisa diisi oleh mitra/teknisi kalo customer pilih waktu bebas
    private String timeOfService;   // HH:mm bisa diisi oleh mitra/teknisi kalo customer pilih waktu bebas
    private boolean serviceTimeFree;
    private long serviceTimestamp;
    private long updatedTimestamp;  // the time when server received order. bisa dipakai utk start timeout
    private long createdTimestamp;
    private String updatedBy;

    public OrderBucket() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
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

    public long getServiceTimestamp() {
        return serviceTimestamp;
    }

    public void setServiceTimestamp(long serviceTimestamp) {
        this.serviceTimestamp = serviceTimestamp;
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

    public boolean isServiceTimeFree() {
        return serviceTimeFree;
    }

    public void setServiceTimeFree(boolean serviceTimeFree) {
        this.serviceTimeFree = serviceTimeFree;
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

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getDateOfService() {
        return dateOfService;
    }

    public void setDateOfService(String dateOfService) {
        this.dateOfService = dateOfService;
    }

    public String getTimeOfService() {
        return timeOfService;
    }

    public void setTimeOfService(String timeOfService) {
        this.timeOfService = timeOfService;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public String toString() {
        return "OrderBucket{" +
                "uid='" + uid + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", addressByGoogle='" + addressByGoogle + '\'' +
                ", partyId='" + partyId + '\'' +
                ", technicianId='" + technicianId + '\'' +
                ", technicianName='" + technicianName + '\'' +
                ", statusId='" + statusId + '\'' +
                ", statusDetailId='" + statusDetailId + '\'' +
                ", statusComment='" + statusComment + '\'' +
                ", acCount=" + acCount +
                ", minuteExtra=" + minuteExtra +
                ", dateOfService='" + dateOfService + '\'' +
                ", timeOfService='" + timeOfService + '\'' +
                ", serviceTimeFree=" + serviceTimeFree +
                ", serviceTimestamp=" + serviceTimestamp +
                ", updatedTimestamp=" + updatedTimestamp +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
