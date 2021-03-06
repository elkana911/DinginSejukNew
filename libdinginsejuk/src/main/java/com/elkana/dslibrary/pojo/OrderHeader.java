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
    private int serviceType;        // 1: quick service, 2 schedule service
    private String statusId;
    private String statusDetailId;
    private String statusComment;   // diperlukan jika Server cancel dan customer diberikan alasan mengapa di cancel
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
    private int ratingByTechnician; //0 - 50
    /**
     * waktu yg diperlukan utk status baru(any status) sebelum dinyatakan CANCELLED_BY_TIMER. hanya berlaku utk status sebelum PAYMENT
     * jgn bingung dengan minuteExtra di orderBucket, logikanya minuteExtra adalh waktu yg diberikan oleh mitra bg semua teknisi,
     * sedangkan life_per_status_minute seperti waktu toleransi dari customer
     * sehingga value life_per_status_minute harus lebih lama dari minuteExtra
     *
     * Untuk saat ini hanya dipakai utk STATUS yg CREATED
     *
     * @see #updatedStatusTimestamp
     */
    private int life_per_status_minute;
    private String ratingCustomerComments;
    private String ratingTechnicianComments;
    private String problem;
    private boolean serviceTimeFree;    // see mobilesetup.serviceTimeFreeDecisionType
    private int serviceTimeFreeDecisionType;    // see mobilesetup.serviceTimeFreeDecisionType
    private String dateOfService;   // yyyyMMdd.
    private String timeOfService;   //HH:mm bisa diisi oleh mitra/teknisi kalo customer pilih waktu bebas
    private String phone;
    private int rescheduleCounter;
    private long serviceTimestamp; // gabungan dateOfService & timeOfService
    private long pleasePayAmount;
    private long createdTimestamp;
    private long updatedStatusTimestamp;
    private long updatedTimestamp;
    private String updatedBy;

    public OrderHeader() {
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

    public String getStatusComment() {
        return statusComment;
    }

    public void setStatusComment(String statusComment) {
        this.statusComment = statusComment;
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

    public int getRatingByTechnician() {
        return ratingByTechnician;
    }

    public void setRatingByTechnician(int ratingByTechnician) {
        this.ratingByTechnician = ratingByTechnician;
    }

    public String getRatingCustomerComments() {
        return ratingCustomerComments;
    }

    public void setRatingCustomerComments(String ratingCustomerComments) {
        this.ratingCustomerComments = ratingCustomerComments;
    }

    public String getRatingTechnicianComments() {
        return ratingTechnicianComments;
    }

    public void setRatingTechnicianComments(String ratingTechnicianComments) {
        this.ratingTechnicianComments = ratingTechnicianComments;
    }

    public int getLife_per_status_minute() {
        return life_per_status_minute;
    }

    public void setLife_per_status_minute(int life_per_status_minute) {
        this.life_per_status_minute = life_per_status_minute;
    }

    public long getUpdatedStatusTimestamp() {
        return updatedStatusTimestamp;
    }

    public void setUpdatedStatusTimestamp(long updatedStatusTimestamp) {
        this.updatedStatusTimestamp = updatedStatusTimestamp;
    }

    public boolean isServiceTimeFree() {
        return serviceTimeFree;
    }

    public void setServiceTimeFree(boolean serviceTimeFree) {
        this.serviceTimeFree = serviceTimeFree;
    }

    public int getServiceTimeFreeDecisionType() {
        return serviceTimeFreeDecisionType;
    }

    public void setServiceTimeFreeDecisionType(int serviceTimeFreeDecisionType) {
        this.serviceTimeFreeDecisionType = serviceTimeFreeDecisionType;
    }

    public long getServiceTimestamp() {
        return serviceTimestamp;
    }

    public void setServiceTimestamp(long serviceTimestamp) {
        this.serviceTimestamp = serviceTimestamp;
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
                ", statusComment='" + statusComment + '\'' +
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
                ", ratingByTechnician=" + ratingByTechnician +
                ", life_per_status_minute=" + life_per_status_minute +
                ", ratingCustomerComments='" + ratingCustomerComments + '\'' +
                ", ratingTechnicianComments='" + ratingTechnicianComments + '\'' +
                ", problem='" + problem + '\'' +
                ", serviceTimeFree=" + serviceTimeFree +
                ", serviceTimeFreeDecisionType=" + serviceTimeFreeDecisionType +
                ", dateOfService='" + dateOfService + '\'' +
                ", timeOfService='" + timeOfService + '\'' +
                ", phone='" + phone + '\'' +
                ", rescheduleCounter=" + rescheduleCounter +
                ", serviceTimestamp=" + serviceTimestamp +
                ", pleasePayAmount=" + pleasePayAmount +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedStatusTimestamp=" + updatedStatusTimestamp +
                ", updatedTimestamp=" + updatedTimestamp +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
