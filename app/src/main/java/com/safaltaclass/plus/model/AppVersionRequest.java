package com.safaltaclass.plus.model;

import com.google.gson.annotations.SerializedName;

public class AppVersionRequest {

    @SerializedName("app_version")
    private String app_version;
    @SerializedName("app_action")
    private String app_action;

    public AppVersionRequest(String version, String action) {
        this.app_version = version;
        this.app_action = action;
    }
}
