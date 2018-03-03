package com.elkana.teknisi.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 16-Dec-17.
 */

public class MitraReg extends RealmObject implements Serializable {
    @PrimaryKey
    private String mitraId;

    public String getMitraId() {
        return mitraId;
    }

    public void setMitraId(String mitraId) {
        this.mitraId = mitraId;
    }

    @Override
    public String toString() {
        return "MitraReg{" +
                "mitraId='" + mitraId + '\'' +
                '}';
    }
}
