package com.elkana.dslibrary.pojo.mitra;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 20-Oct-17.
 */

public class SubServiceType extends RealmObject implements Serializable{
    @PrimaryKey
    private int typeId;
    private String typeName;
    private String typeNameBahasa;
    private boolean visible;

    public SubServiceType() {
        this.visible = true;
    }

    public SubServiceType(int typeId, String typeName, String typeNameBahasa) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.typeNameBahasa = typeNameBahasa;
        this.visible = true;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeNameBahasa() {
        return typeNameBahasa;
    }

    public void setTypeNameBahasa(String typeNameBahasa) {
        this.typeNameBahasa = typeNameBahasa;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "SubServiceType{" +
                "typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", typeNameBahasa='" + typeNameBahasa + '\'' +
                ", visible=" + visible +
                '}';
    }
}
