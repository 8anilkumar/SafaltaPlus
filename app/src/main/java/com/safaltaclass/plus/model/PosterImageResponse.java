package com.safaltaclass.plus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PosterImageResponse {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("internal_msg")
    @Expose
    private String internalMsg;
    @SerializedName("data")
    @Expose
    private List<PosterImage> data = null;

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

    public String getInternalMsg() {
        return internalMsg;
    }

    public void setInternalMsg(String internalMsg) {
        this.internalMsg = internalMsg;
    }

    public List<PosterImage> getData() {
        return data;
    }

    public void setData(List<PosterImage> data) {
        this.data = data;
    }
}
