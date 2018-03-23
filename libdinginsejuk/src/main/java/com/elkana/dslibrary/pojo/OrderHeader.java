package com.elkana.dslibrary.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 30-Oct-17.
 */

public class OrderHeader extends RealmObject implements Serializable {

    @PrimaryKey
    private String uid;
    private String invoiceNo;
    private String customerId;
    private String customerName;
    private int serviceType;
    private String statusId;
    private String statusDetailId;
    private String partyId;         // why string ? for future reference could be unique string, even Mitra.id is a long
    private String partyName;
    private String technicianId;    // why string ? for future reference could be unique string
    private String technicianName;
    private String addressId;
    private String addressByGoogle; // supaya kalo user delete tetep remain
    private String assignmentId;
    private String latitude;
    private String longitude;
    private int jumlahAC;
    private int ratingByCustomer;   //0 - 50
    private String ratingComments;
    private String problem;
    private String dateOfService;
    private String timeOfService;
    private String phone;
    private int rescheduleCounter;
    private long bookingTimestamp; // gabungan dateOfService & timeOfService
    private long pleasePayAmount;
    private long createdTimestamp;
    private long updatedTimestamp;
    private String updatedBy;

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

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusDetailId() {
        return statusDetailId;
    }

    public void setStatusDetailId(String statusDetailId) {
        this.statusDetailId = statusDetailId;
    }

    public long getBookingTimestamp() {
        return bookingTimestamp;
    }

    public void setBookingTimestamp(long bookingTimestamp) {
        this.bookingTimestamp = bookingTimestamp;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getJumlahAC() {
        return jumlahAC;
    }

    public void setJumlahAC(int jumlahAC) {
        this.jumlahAC = jumlahAC;
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

    public int getRatingByCustomer() {
        return ratingByCustomer;
    }

    public void setRatingByCustomer(int ratingByCustomer) {
        this.ratingByCustomer = ratingByCustomer;
    }

    public String getRatingComments() {
        return ratingComments;
    }

    public void setRatingComments(String ratingComments) {
        this.ratingComments = ratingComments;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
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

    public String getAddressByGoogle() {
        return addressByGoogle;
    }

    public void setAddressByGoogle(String addressByGoogle) {
        this.addressByGoogle = addressByGoogle;
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

    public int getRescheduleCounter() {
        return rescheduleCounter;
    }

    public void setRescheduleCounter(int rescheduleCounter) {
        this.rescheduleCounter = rescheduleCounter;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getPleasePayAmount() {
        return pleasePayAmount;
    }

    public void setPleasePayAmount(long pleasePayAmount) {
        this.pleasePayAmount = pleasePayAmount;
    }

    @Override
    public String toString() {
        return "OrderHeader{" +
                "uid='" + uid + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", serviceType=" + serviceType +
                ", statusId='" + statusId + '\'' +
                ", statusDetailId='" + statusDetailId + '\'' +
                ", partyId='" + partyId + '\'' +
                ", partyName='" + partyName + '\'' +
                ", technicianId='" + technicianId + '\'' +
                ", technicianName='" + technicianName + '\'' +
                ", addressId='" + addressId + '\'' +
                ", addressByGoogle='" + addressByGoogle + '\'' +
                ", assignmentId='" + assignmentId + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", jumlahAC=" + jumlahAC +
                ", ratingByCustomer=" + ratingByCustomer +
                ", ratingComments='" + ratingComments + '\'' +
                ", problem='" + problem + '\'' +
                ", dateOfService='" + dateOfService + '\'' +
                ", timeOfService='" + timeOfService + '\'' +
                ", phone='" + phone + '\'' +
                ", rescheduleCounter=" + rescheduleCounter +
                ", bookingTimestamp=" + bookingTimestamp +
                ", pleasePayAmount=" + pleasePayAmount +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedTimestamp=" + updatedTimestamp +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
