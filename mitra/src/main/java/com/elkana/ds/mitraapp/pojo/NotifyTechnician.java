package com.elkana.ds.mitraapp.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Table ini diperlukan RVAdapterOrderList spy sistem bisa ngirim notifikasi ke teknisi spy terhindar double send
 * Berbeda dengan object NotifyNewOrderItem, table ini menggunakan uid
 * orders/ac/pending/mitra/<mitraId>
 * Created by Eric on 07-Mar-18.
 */

public class NotifyTechnician extends RealmObject implements Serializable {
    @PrimaryKey
    private String uid;
    private String orderId;
    private String techId;
    private int counter;
    private long timestamp;

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

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "NotifyTechnician{" +
                "uid='" + uid + '\'' +
                ", orderId='" + orderId + '\'' +
                ", techId='" + techId + '\'' +
                ", counter=" + counter +
                ", timestamp=" + timestamp +
                '}';
    }
}
