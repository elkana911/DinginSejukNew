package com.elkana.dslibrary.pojo.mitra;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PriceInfo extends RealmObject implements Serializable{
    @PrimaryKey
    private String mitraId;

    private String info;
    private long updatedTimestamp;

    public String getMitraId() {
        return mitraId;
    }

    public void setMitraId(String mitraId) {
        this.mitraId = mitraId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String toString() {
        return "PriceInfo{" +
                "mitraId='" + mitraId + '\'' +
                ", info='" + info + '\'' +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}
