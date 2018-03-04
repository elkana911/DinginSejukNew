package com.elkana.ds.mitraapp.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 20-Oct-17.
 */
//see TmpMitra and DataUtil.cloneMitra
//mitra/ac/<mitraId>/technicians/<technicianId>
public class TechnicianReg extends RealmObject implements Serializable {

    @PrimaryKey
    private String techId;
    private long joinDate;
    private String name;
    private boolean suspend;    // if absen, berlaku sehari ?
    private int orderTodayCount;

    public TechnicianReg() {
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuspend() {
        return suspend;
    }

    public void setSuspend(boolean suspend) {
        this.suspend = suspend;
    }

    @Exclude
    public int getOrderTodayCount() {
        return orderTodayCount;
    }

    public void setOrderTodayCount(int orderTodayCount) {
        this.orderTodayCount = orderTodayCount;
    }

    @Override
    public String toString() {
        return "TechnicianReg{" +
                "techId='" + techId + '\'' +
                ", joinDate=" + joinDate +
                ", name='" + name + '\'' +
                ", suspend=" + suspend +
                ", orderTodayCount=" + orderTodayCount +
                '}';
    }
}
