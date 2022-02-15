package com.safaltaclass.plus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForgetResponsebody {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("user_msg")
    @Expose
    private String userMsg;

    @SerializedName("domain")
    @Expose
    private String domain;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

}
