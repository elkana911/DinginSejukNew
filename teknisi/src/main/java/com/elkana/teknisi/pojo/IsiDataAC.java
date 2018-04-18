package com.elkana.teknisi.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class IsiDataAC extends RealmObject implements Serializable {

    @PrimaryKey
    private long uid;   // timestamp
    private String scanContent;
    private String scanFormat;
    private String merkAC;
    private String dayaAC;
    private String tahunPemasangan;
    private String tipeAC;
    private String notes;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getScanContent() {
        return scanContent;
    }

    public void setScanContent(String scanContent) {
        this.scanContent = scanContent;
    }

    public String getScanFormat() {
        return scanFormat;
    }

    public void setScanFormat(String scanFormat) {
        this.scanFormat = scanFormat;
    }

    public String getMerkAC() {
        return merkAC;
    }

    public void setMerkAC(String merkAC) {
        this.merkAC = merkAC;
    }

    public String getDayaAC() {
        return dayaAC;
    }

    public void setDayaAC(String dayaAC) {
        this.dayaAC = dayaAC;
    }

    public String getTahunPemasangan() {
        return tahunPemasangan;
    }

    public void setTahunPemasangan(String tahunPemasangan) {
        this.tahunPemasangan = tahunPemasangan;
    }

    public String getTipeAC() {
        return tipeAC;
    }

    public void setTipeAC(String tipeAC) {
        this.tipeAC = tipeAC;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "IsiDataAC{" +
                "uid=" + uid +
                ", scanContent='" + scanContent + '\'' +
                ", scanFormat='" + scanFormat + '\'' +
                ", merkAC='" + merkAC + '\'' +
                ", dayaAC='" + dayaAC + '\'' +
                ", tahunPemasangan='" + tahunPemasangan + '\'' +
                ", tipeAC='" + tipeAC + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
