package com.example.clock_o_mentia.Patient.Models;

public class DoctorModel {

    private String name;
    private Integer age;
    private String email;
    private String phoneNum;
    private double latitude;
    private double longitude;
    private String gender;
    private String cerificate_link;
    private String profilePhoto_link;

    public DoctorModel() {

    }

    public DoctorModel(String name, Integer age, String email, String phoneNum, double latitude, double longitude, String gender, String cerificate_link, String profilePhoto_link) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.phoneNum = phoneNum;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gender = gender;
        this.cerificate_link = cerificate_link;
        this.profilePhoto_link = profilePhoto_link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCerificate_link() {
        return cerificate_link;
    }

    public void setCerificate_link(String cerificate_link) {
        this.cerificate_link = cerificate_link;
    }

    public String getProfilePhoto_link() {
        return profilePhoto_link;
    }

    public void setProfilePhoto_link(String profilePhoto_link) {
        this.profilePhoto_link = profilePhoto_link;
    }
}
