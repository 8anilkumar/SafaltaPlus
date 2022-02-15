package com.safaltaclass.plus.model;

import com.google.gson.annotations.SerializedName;

public class ForgetRequestBody {

    @SerializedName("action")
    private String action;
    @SerializedName("login")
    private String login;


    public ForgetRequestBody(String action, String login) {
        this.action = action;
        this.login = login;
    }
}
