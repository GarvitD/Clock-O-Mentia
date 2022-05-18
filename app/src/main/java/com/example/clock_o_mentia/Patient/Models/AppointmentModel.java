package com.example.clock_o_mentia.Patient.Models;

import java.io.Serializable;

public class AppointmentModel implements Serializable {

    private String name;
    private String dateTime;
    private int status;
    private String detailedInfo;
    private String reportUrl;
    private String userId;
    private String doctorName;
    private String doctorEmail;

    public AppointmentModel() {}


    public AppointmentModel(String name, String dateTime, int status, String detailedInfo, String reportUrl, String userId, String doctorName, String doctorEmail) {
        this.name = name;
        this.dateTime = dateTime;
        this.status = status;
        this.detailedInfo = detailedInfo;
        this.reportUrl = reportUrl;
        this.userId = userId;
        this.doctorName = doctorName;
        this.doctorEmail = doctorEmail;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetailedInfo() {
        return detailedInfo;
    }

    public void setDetailedInfo(String detailedInfo) {
        this.detailedInfo = detailedInfo;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}
