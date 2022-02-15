package com.safaltaclass.plus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CourseListData {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("thumb")
    @Expose
    private String thumb;

    @SerializedName("parentcode")
    @Expose
    private String parentcode;



    @SerializedName("nextPageTitle")
    @Expose
    private String nextPageTitle;


    public CourseListData(String code, String title, String validUpto, String description,String thumb) {
        this.code = code;
        this.title = title;
        this.date = validUpto;
        this.description = description;
        this.thumb = thumb;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getParentcode() {
        return parentcode;
    }

    public void setParentcode(String parentcode) {
        this.parentcode = parentcode;
    }

    public String getNextPageTitle() {
        return nextPageTitle;
    }

    public void setNextPageTitle(String nextPageTitle) {
        this.nextPageTitle = nextPageTitle;
    }

}
