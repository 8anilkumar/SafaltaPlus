package com.safaltaclass.plus.model;

import com.google.gson.annotations.SerializedName;

public class SubCourseRequestBody {

    @SerializedName("parentcode")
    private String parentCode;


    public SubCourseRequestBody(String parentCode) {
        this.parentCode = parentCode;
    }
}
