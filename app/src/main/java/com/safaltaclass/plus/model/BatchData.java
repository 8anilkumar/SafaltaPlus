package com.safaltaclass.plus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BatchData {

    @SerializedName("batchid")
    @Expose
    private String batchid;
    @SerializedName("batchcode")
    @Expose
    private String batchcode;
    @SerializedName("batchname")
    @Expose
    private String batchname;
    @SerializedName("batchdescription")
    @Expose
    private String batchdescription;
    @SerializedName("datestart")
    @Expose
    private String datestart;
    @SerializedName("dateend")
    @Expose
    private String dateend;
    @SerializedName("fees")
    @Expose
    private String fees;
    @SerializedName("deliverymode")
    @Expose
    private String deliverymode;
    @SerializedName("dowschedule")
    @Expose
    private String dowschedule;
    @SerializedName("schedulevariation")
    @Expose
    private String schedulevariation;
    @SerializedName("attemptdue")
    @Expose
    private String attemptdue;
    @SerializedName("termsandconditions")
    @Expose
    private String termsandconditions;
    @SerializedName("courseid")
    @Expose
    private String courseid;
    @SerializedName("coursename")
    @Expose
    private String coursename;
    @SerializedName("levelid")
    @Expose
    private String levelid;
    @SerializedName("levelname")
    @Expose
    private String levelname;
    @SerializedName("subjectid")
    @Expose
    private String subjectid;
    @SerializedName("sequencecode")
    @Expose
    private String sequencecode;
    @SerializedName("subjectname")
    @Expose
    private String subjectname;
    @SerializedName("facultyname")
    @Expose
    private String facultyname;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("centerid")
    @Expose
    private String centerid;
    @SerializedName("centername")
    @Expose
    private String centername;
    @SerializedName("admissionthrough")
    @Expose
    private String admissionthrough;
    @SerializedName("admissiontill")
    @Expose
    private String admissiontill;
    @SerializedName("admissionOpen")
    @Expose
    private String admissionOpen;
    @SerializedName("attemptdue_csv")
    @Expose
    private String attemptdueCsv;
    @SerializedName("tags")
    @Expose
    private String tags;

    public String getBatchid() {
        return batchid;
    }

    public void setBatchid(String batchid) {
        this.batchid = batchid;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }

    public String getBatchname() {
        return batchname;
    }

    public void setBatchname(String batchname) {
        this.batchname = batchname;
    }

    public String getBatchdescription() {
        return batchdescription;
    }

    public void setBatchdescription(String batchdescription) {
        this.batchdescription = batchdescription;
    }

    public String getDatestart() {
        return datestart;
    }

    public void setDatestart(String datestart) {
        this.datestart = datestart;
    }

    public String getDateend() {
        return dateend;
    }

    public void setDateend(String dateend) {
        this.dateend = dateend;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public String getDowschedule() {
        return dowschedule;
    }

    public void setDowschedule(String dowschedule) {
        this.dowschedule = dowschedule;
    }

    public String getSchedulevariation() {
        return schedulevariation;
    }

    public void setSchedulevariation(String schedulevariation) {
        this.schedulevariation = schedulevariation;
    }

    public String getAttemptdue() {
        return attemptdue;
    }

    public void setAttemptdue(String attemptdue) {
        this.attemptdue = attemptdue;
    }

    public String getTermsandconditions() {
        return termsandconditions;
    }

    public void setTermsandconditions(String termsandconditions) {
        this.termsandconditions = termsandconditions;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getLevelid() {
        return levelid;
    }

    public void setLevelid(String levelid) {
        this.levelid = levelid;
    }

    public String getLevelname() {
        return levelname;
    }

    public void setLevelname(String levelname) {
        this.levelname = levelname;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getSequencecode() {
        return sequencecode;
    }

    public void setSequencecode(String sequencecode) {
        this.sequencecode = sequencecode;
    }

    public String getSubjectname() {
        return subjectname;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
    }

    public String getFacultyname() {
        return facultyname;
    }

    public void setFacultyname(String facultyname) {
        this.facultyname = facultyname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCenterid() {
        return centerid;
    }

    public void setCenterid(String centerid) {
        this.centerid = centerid;
    }

    public String getCentername() {
        return centername;
    }

    public void setCentername(String centername) {
        this.centername = centername;
    }

    public String getAdmissionthrough() {
        return admissionthrough;
    }

    public void setAdmissionthrough(String admissionthrough) {
        this.admissionthrough = admissionthrough;
    }

    public String getAdmissiontill() {
        return admissiontill;
    }

    public void setAdmissiontill(String admissiontill) {
        this.admissiontill = admissiontill;
    }

    public String getAdmissionOpen() {
        return admissionOpen;
    }

    public void setAdmissionOpen(String admissionOpen) {
        this.admissionOpen = admissionOpen;
    }

    public String getAttemptdueCsv() {
        return attemptdueCsv;
    }

    public void setAttemptdueCsv(String attemptdueCsv) {
        this.attemptdueCsv = attemptdueCsv;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
