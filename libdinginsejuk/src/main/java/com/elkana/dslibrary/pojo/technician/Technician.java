package com.elkana.dslibrary.pojo.technician;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 20-Oct-17.
 */
//see TmpMitra and DataUtil.cloneMitra
public class Technician extends RealmObject implements Serializable {

    @PrimaryKey
    private String uid;
    private String name;
    private String address;
    private String email;
    private String phone1;
    private String phone2;
    private int workingHourStart;   //8
    private int workingHourEnd;     //17
    private int rating;

    private boolean enable;
    private boolean visible;

    public Technician() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getWorkingHourStart() {
        return workingHourStart;
    }

    public void setWorkingHourStart(int workingHourStart) {
        this.workingHourStart = workingHourStart;
    }

    public int getWorkingHourEnd() {
        return workingHourEnd;
    }

    public void setWorkingHourEnd(int workingHourEnd) {
        this.workingHourEnd = workingHourEnd;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Technician{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone1='" + phone1 + '\'' +
                ", phone2='" + phone2 + '\'' +
                ", workingHourStart=" + workingHourStart +
                ", workingHourEnd=" + workingHourEnd +
                ", rating=" + rating +
                ", enable=" + enable +
                ", visible=" + visible +
                '}';
    }
}
