package com.safaltaclass.plus.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequestBody {

    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    public LoginRequestBody(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
