package com.example.clock_o_mentia.Patient.Models;

public class AppointmentModel {

    private String name;
    private String dateTime;
    private int status;
    private String detailedInfo;
    private String reportUrl;

    public AppointmentModel() {}

    public AppointmentModel(String name, String dateTime, int status, String detailedInfo, String reportUrl) {
        this.name = name;
        this.dateTime = dateTime;
        this.status = status;
        this.detailedInfo = detailedInfo;
        this.reportUrl = reportUrl;
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
