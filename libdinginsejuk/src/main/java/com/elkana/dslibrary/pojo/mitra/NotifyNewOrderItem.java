package com.elkana.dslibrary.pojo.mitra;

/**
 * Created by Eric on 10-Mar-18.
 */

public class NotifyNewOrderItem {
    private String orderId;
    private String mitraId;
    private String techId;
    private String address;
    private int acCount;
    private String customerId;
    private String customerName;
    private String dateOfService;   // yyyyMMdd.
    private String timeOfService;   // HH:mm bisa diisi oleh mitra/teknisi kalo customer pilih waktu bebas
    private boolean serviceTimeFree;
    private long serviceTimestamp;    // order timestamp
//    private long timestamp; // inserted timestamp. diganti mitraTimestamp
    private long mitraTimestamp;    // dibuat utk timer dihitung dr waktunya mitra, supaya tetap sinkron dengan timer di teknisi. deprecated, see createdTimestamp
    private int minuteExtra;    // buat timer. diisi dari server. defaultnya 10 menit. utk develop 5 menit cukup.
    private int mitraOpenTime;  // perlu tau spy teknisi bisa atur waktu dengan mitra-nya
    private int mitraCloseTime;
    private long createdTimestamp;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMitraId() {
        return mitraId;
    }

    public void setMitraId(String mitraId) {
        this.mitraId = mitraId;
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAcCount() {
        return acCount;
    }

    public void setAcCount(int acCount) {
        this.acCount = acCount;
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

    public long getServiceTimestamp() {
        return serviceTimestamp;
    }

    public void setServiceTimestamp(long serviceTimestamp) {
        this.serviceTimestamp = serviceTimestamp;
    }

    public boolean isServiceTimeFree() {
        return serviceTimeFree;
    }

    public void setServiceTimeFree(boolean serviceTimeFree) {
        this.serviceTimeFree = serviceTimeFree;
    }

    public long getMitraTimestamp() {
        return mitraTimestamp;
    }

    public void setMitraTimestamp(long mitraTimestamp) {
        this.mitraTimestamp = mitraTimestamp;
    }

    public int getMinuteExtra() {
        return minuteExtra;
    }

    public void setMinuteExtra(int minuteExtra) {
        this.minuteExtra = minuteExtra;
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

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public String toString() {
        return "NotifyNewOrderItem{" +
                "orderId='" + orderId + '\'' +
                ", mitraId='" + mitraId + '\'' +
                ", techId='" + techId + '\'' +
                ", address='" + address + '\'' +
                ", acCount=" + acCount +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", dateOfService='" + dateOfService + '\'' +
                ", timeOfService='" + timeOfService + '\'' +
                ", serviceTimeFree=" + serviceTimeFree +
                ", serviceTimestamp=" + serviceTimestamp +
                ", mitraTimestamp=" + mitraTimestamp +
                ", minuteExtra=" + minuteExtra +
                ", mitraOpenTime=" + mitraOpenTime +
                ", mitraCloseTime=" + mitraCloseTime +
                ", createdTimestamp=" + createdTimestamp +
                '}';
    }
}
