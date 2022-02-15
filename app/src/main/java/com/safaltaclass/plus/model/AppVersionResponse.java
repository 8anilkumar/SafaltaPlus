package com.safaltaclass.plus.model;

import com.google.gson.annotations.SerializedName;

public class AppVersionResponse {
    @SerializedName("code")
    String code;
    @SerializedName("action")
    String action;
    @SerializedName("status")
    boolean status;
    @SerializedName("user_msg")
    String message;

    public boolean isSuccess(){
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public String getAction() {
        return action;
    }
}
