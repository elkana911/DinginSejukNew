package com.elkana.dslibrary.pojo.mitra;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 24-Mar-18.
 */

public class JobsCancelled extends RealmObject implements Serializable{

    @PrimaryKey
    private long id;
    private String techId;
    private String orderId;
    private String wkt;

    public JobsCancelled() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    @Override
    public String toString() {
        return "JobsAssigned{" +
                "id=" + id +
                ", techId='" + techId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", wkt='" + wkt + '\'' +
                '}';
    }
}
