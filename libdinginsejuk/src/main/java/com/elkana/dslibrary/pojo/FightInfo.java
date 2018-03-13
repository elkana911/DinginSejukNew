package com.elkana.dslibrary.pojo;

/**
 * Created by Eric on 11-Mar-18.
 */

// see RVAdapterNotifyNewOrderList
public class FightInfo {
    private String techId;
    private String techName;
    private String orderId;
    private String custId;
    private long timestamp;

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FightInfo{" +
                "techId='" + techId + '\'' +
                ", techName='" + techName + '\'' +
                ", orderId='" + orderId + '\'' +
                ", custId='" + custId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
