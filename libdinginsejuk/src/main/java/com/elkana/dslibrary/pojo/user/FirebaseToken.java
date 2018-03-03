package com.elkana.dslibrary.pojo.user;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 28-Oct-17.
 */

public class FirebaseToken extends RealmObject implements Serializable {
    @PrimaryKey
    private String token;
    private long timestamp;

    public FirebaseToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FirebaseToken{" +
                "token='" + token + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
