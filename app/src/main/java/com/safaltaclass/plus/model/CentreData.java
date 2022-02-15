package com.safaltaclass.plus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CentreData {

    @SerializedName("centerid")
    @Expose
    private String centerid;
    @SerializedName("centercode")
    @Expose
    private String centercode;
    @SerializedName("centername")
    @Expose
    private String centername;
    @SerializedName("statecode")
    @Expose
    private String statecode;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("locality")
    @Expose
    private String locality;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("pin")
    @Expose
    private String pin;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("web")
    @Expose
    private String web;
    @SerializedName("circle")
    @Expose
    private String circle;
    @SerializedName("subcircle")
    @Expose
    private String subcircle;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("weblist")
    @Expose
    private String weblist;

    public String getCenterid() {
        return centerid;
    }

    public void setCenterid(String centerid) {
        this.centerid = centerid;
    }

    public String getCentercode() {
        return centercode;
    }

    public void setCentercode(String centercode) {
        this.centercode = centercode;
    }

    public String getCentername() {
        return centername;
    }

    public void setCentername(String centername) {
        this.centername = centername;
    }

    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getSubcircle() {
        return subcircle;
    }

    public void setSubcircle(String subcircle) {
        this.subcircle = subcircle;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getWeblist() {
        return weblist;
    }

    public void setWeblist(String weblist) {
        this.weblist = weblist;
    }
}
