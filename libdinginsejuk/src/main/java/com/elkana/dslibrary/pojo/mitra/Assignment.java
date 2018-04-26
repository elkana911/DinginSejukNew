package com.elkana.dslibrary.pojo.mitra;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 13-Nov-17.
 */

public class Assignment extends RealmObject implements Serializable {

    @PrimaryKey
    private String uid;
    private String orderId;         //orderHeader.uid
    private String invoiceNo;
    private String technicianId;
    private String technicianName;
    private String customerId;      //orderHeader.customerId
    private String customerName;    //orderHeader.customerName
    private String customerAddress; //orderHeader.addressId
    private String dateOfService;     //orderHeader.dateOfService
    private String timeOfService;     //orderHeader.timeOfService
    private String latitude;
    private String longitude;
//    private double rate;
    private long startDate; // diisi waktu mulai layanan
    private long endDate;   // diisi waktu menunggu pembayaran
    private long createdDate;
    private int serviceType;
    private String mitraId;
    private String mitraName;
    private int mitraOpenTime;
    private int mitraCloseTime;
    private String statusDetailId;  //should synchron 1 way with orderHeader.statusDetailId
    private String statusComment;   // diperlukan jika Server cancel dan customer diberikan alasan mengapa di cancel
    private long updatedTimestamp;
    private String updatedBy;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
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

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getMitraId() {
        return mitraId;
    }

    public void setMitraId(String mitraId) {
        this.mitraId = mitraId;
    }

    public String getMitraName() {
        return mitraName;
    }

    public void setMitraName(String mitraName) {
        this.mitraName = mitraName;
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

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getMitraOpenTime() {
        return mitraOpenTime;
    }

    public void setMitraOpenTime(int mitraOpenTime) {
        this.mitraOpenTime = mitraOpenTime;
    }

    public int getMitraCloseTime() {
        return mitraCloseTime;
    }

    public void setMitraCloseTime(int mitraCloseTime) {
        this.mitraCloseTime = mitraCloseTime;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "uid='" + uid + '\'' +
                ", orderId='" + orderId + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", technicianId='" + technicianId + '\'' +
                ", technicianName='" + technicianName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", dateOfService='" + dateOfService + '\'' +
                ", timeOfService='" + timeOfService + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createdDate=" + createdDate +
                ", serviceType=" + serviceType +
                ", mitraId='" + mitraId + '\'' +
                ", mitraName='" + mitraName + '\'' +
                ", mitraOpenTime=" + mitraOpenTime +
                ", mitraCloseTime=" + mitraCloseTime +
                ", statusDetailId='" + statusDetailId + '\'' +
                ", statusComment='" + statusComment + '\'' +
                ", updatedTimestamp=" + updatedTimestamp +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}

