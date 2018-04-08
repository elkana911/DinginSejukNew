package com.elkana.dslibrary.pojo.mitra;

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
    private int orderTodayCount;    // dipakai wkt assign manual. utk menampung ada brp jobs yg diassign ke teknisi ini. tidak utk ditaruh di firebase makanya di @Exclude
    private int lastScore;  //0 - infinite

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

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
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
                ", lastScore=" + lastScore +
                '}';
    }
}
