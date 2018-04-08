package com.elkana.dslibrary.firebase;

@Deprecated
public class ResponseCreateBooking {
    private String orderKey;
    private long updatedTimestamp;

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String toString() {
        return "ResponseCreateBooking{" +
                "orderKey='" + orderKey + '\'' +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}
