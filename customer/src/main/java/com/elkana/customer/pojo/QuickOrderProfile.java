package com.elkana.customer.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class QuickOrderProfile extends RealmObject implements Serializable {
    @PrimaryKey
    private long uid;   // also as createdtimestamp
    private String userId;
    private String label;

    private int acCount;
    private String dateOfService;
    private String timeOfService;
    private long serviceTimestamp;
    private boolean serviceTimeFree;

    private String addressId;
    private String problems;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }


    public String getProblems() {
        return problems;
    }

    public void setProblems(String problems) {
        this.problems = problems;
    }

    public int getAcCount() {
        return acCount;
    }

    public void setAcCount(int acCount) {
        this.acCount = acCount;
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

    @Override
    public String toString() {
        return "QuickOrderProfile{" +
                "uid=" + uid +
                ", userId='" + userId + '\'' +
                ", label='" + label + '\'' +
                ", acCount=" + acCount +
                ", dateOfService='" + dateOfService + '\'' +
                ", timeOfService='" + timeOfService + '\'' +
                ", serviceTimestamp=" + serviceTimestamp +
                ", serviceTimeFree=" + serviceTimeFree +
                ", addressId='" + addressId + '\'' +
                ", problems='" + problems + '\'' +
                '}';
    }
}
