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
    private long orderTimestamp;    // order timestamp
    private long timestamp; // inserted timestamp
    private long mitraTimestamp;    // dibuat utk timer dihitung dr waktunya mitra, supaya tetap sinkron dengan timer di teknisi

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

    public long getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(long orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getMitraTimestamp() {
        return mitraTimestamp;
    }

    public void setMitraTimestamp(long mitraTimestamp) {
        this.mitraTimestamp = mitraTimestamp;
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
                ", orderTimestamp=" + orderTimestamp +
                ", timestamp=" + timestamp +
                ", mitraTimestamp=" + mitraTimestamp +
                '}';
    }
}
