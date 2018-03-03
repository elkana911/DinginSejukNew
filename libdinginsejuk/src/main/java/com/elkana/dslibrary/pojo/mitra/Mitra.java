package com.elkana.dslibrary.pojo.mitra;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 20-Oct-17.
 */
// see TmpMitra and DataUtil.cloneMitra
// and BasicInfo of TopMitra project
public class Mitra extends RealmObject implements Serializable {

    @PrimaryKey
    private String uid;
    private String name;
    private String status;

    private int workingHourStart;   //8
    private int workingHourEnd;     //17
    private int rating;     // 0 - 50
    private boolean enable;
    private boolean visible;

    private String addressLabel;
    private String addressByGoogle;
    private String email;
    private String latitude;
    private String longitude;
    private String phone1;
    private String fax1;
    private String phone2;
    private String fax2;
    private int partyTypeId;

    private int userType;   // 10: consumer, 30: technician, 20:mitra, 99:server
    private long createdTimestamp;
    private long updatedTimestamp;

    public Mitra() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(String addressLabel) {
        this.addressLabel = addressLabel;
    }

    public String getAddressByGoogle() {
        return addressByGoogle;
    }

    public void setAddressByGoogle(String addressByGoogle) {
        this.addressByGoogle = addressByGoogle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getFax1() {
        return fax1;
    }

    public void setFax1(String fax1) {
        this.fax1 = fax1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getFax2() {
        return fax2;
    }

    public void setFax2(String fax2) {
        this.fax2 = fax2;
    }

    public int getPartyTypeId() {
        return partyTypeId;
    }

    public void setPartyTypeId(int partyTypeId) {
        this.partyTypeId = partyTypeId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String toString() {
        return "Mitra{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", workingHourStart=" + workingHourStart +
                ", workingHourEnd=" + workingHourEnd +
                ", rating=" + rating +
                ", enable=" + enable +
                ", visible=" + visible +
                ", addressLabel='" + addressLabel + '\'' +
                ", addressByGoogle='" + addressByGoogle + '\'' +
                ", email='" + email + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", phone1='" + phone1 + '\'' +
                ", fax1='" + fax1 + '\'' +
                ", phone2='" + phone2 + '\'' +
                ", fax2='" + fax2 + '\'' +
                ", partyTypeId=" + partyTypeId +
                ", userType=" + userType +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}
