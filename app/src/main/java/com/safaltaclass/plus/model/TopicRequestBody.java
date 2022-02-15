package com.safaltaclass.plus.model;

import com.google.gson.annotations.SerializedName;

public class TopicRequestBody {
    @SerializedName("course_code")
    private String courseCode;
    @SerializedName("date")
    private String date;
    @SerializedName("category")
    private String category;
    @SerializedName("contentformat")
    private String contentFormat;
    @SerializedName("title")
    private String title;
    @SerializedName("search")
    private String search;
    @SerializedName("latest")
    private String latest;
    @SerializedName("cp")
    private String cp;


    public TopicRequestBody(String courseCode, String date, String category, String contentFormat, String title, String search,String latest,String cp) {
        this.courseCode = courseCode;
        this.date = date;
        this.category = category;
        this.contentFormat = contentFormat;
        this.title = title;
        this.search = search;
        this.latest = latest;
        this.cp = cp;
    }
}
