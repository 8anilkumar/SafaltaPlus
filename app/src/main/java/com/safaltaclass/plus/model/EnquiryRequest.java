package com.safaltaclass.plus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnquiryRequest {

    @SerializedName("visitorname")
    @Expose
    private String visitorname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("enquiryfor")
    @Expose
    private String enquiryfor;
    @SerializedName("remarks")
    @Expose
    private String remarks;

    public EnquiryRequest(String visitorname, String email, String mobile, String enquiryfor, String remarks) {
        this.visitorname = visitorname;
        this.email = email;
        this.mobile = mobile;
        this.enquiryfor = enquiryfor;
        this.remarks = remarks;
    }

    public String getVisitorname() {
        return visitorname;
    }

    public void setVisitorname(String visitorname) {
        this.visitorname = visitorname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEnquiryfor() {
        return enquiryfor;
    }

    public void setEnquiryfor(String enquiryfor) {
        this.enquiryfor = enquiryfor;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
