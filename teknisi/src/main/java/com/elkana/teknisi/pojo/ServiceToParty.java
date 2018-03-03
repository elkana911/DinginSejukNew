package com.elkana.teknisi.pojo;

/**
 * Created by Eric on 18-Nov-17.
 * Dont send this object back to firebase !
 */
public class ServiceToParty {
    private int serviceTypeId;
    private int priceSchemeId;
    private String serviceLabel;
    private String serviceLabelBahasa;
    private double basicFare;

    public ServiceToParty() {
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public int getPriceSchemeId() {
        return priceSchemeId;
    }

    public void setPriceSchemeId(int priceSchemeId) {
        this.priceSchemeId = priceSchemeId;
    }

    public double getBasicFare() {
        return basicFare;
    }

    public void setBasicFare(double basicFare) {
        this.basicFare = basicFare;
    }

    public String getServiceLabel() {
        return serviceLabel;
    }

    public void setServiceLabel(String serviceLabel) {
        this.serviceLabel = serviceLabel;
    }

    public String getServiceLabelBahasa() {
        return serviceLabelBahasa;
    }

    public void setServiceLabelBahasa(String serviceLabelBahasa) {
        this.serviceLabelBahasa = serviceLabelBahasa;
    }

    @Override
    public String toString() {
        return "ServiceToParty{" +
                "serviceTypeId=" + serviceTypeId +
                ", priceSchemeId=" + priceSchemeId +
                ", serviceLabel='" + serviceLabel + '\'' +
                ", serviceLabelBahasa='" + serviceLabelBahasa + '\'' +
                ", basicFare=" + basicFare +
                '}';
    }
}
