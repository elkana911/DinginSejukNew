package com.elkana.teknisi.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReminderAssignment extends RealmObject implements Serializable{

    @PrimaryKey
    private String uid;  // = assignment.uid

    private int uniqueCode; // utk notifikasiID krn harus unik
    private int remindType; // 1 hour, 2 hour, 10 menit, ontime
    private long reminderTime;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(int uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public int getRemindType() {
        return remindType;
    }

    public void setRemindType(int remindType) {
        this.remindType = remindType;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    @Override
    public String toString() {
        return "ReminderAssignment{" +
                "uid='" + uid + '\'' +
                ", uniqueCode=" + uniqueCode +
                ", remindType=" + remindType +
                ", reminderTime=" + reminderTime +
                '}';
    }
}
