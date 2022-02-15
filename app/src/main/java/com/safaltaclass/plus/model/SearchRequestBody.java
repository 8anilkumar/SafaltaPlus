package com.safaltaclass.plus.model;

import com.google.gson.annotations.SerializedName;

public class SearchRequestBody {

    @SerializedName("course_code")
    private String courseCode;
    @SerializedName("search_text")
    private String searchText;
    @SerializedName("search_date")
    private String searchDate;
    @SerializedName("cp")
    private String cp;

    public SearchRequestBody(String courseCode, String searchText, String searchDate, String cp) {
        this.courseCode = courseCode;
        this.searchText = searchText;
        this.searchDate = searchDate;
        this.cp = cp;
    }
}
