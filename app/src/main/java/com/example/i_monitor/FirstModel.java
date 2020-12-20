package com.example.i_monitor;

//Model for I-Visitor app data
public class FirstModel {
    private String name;
    private int age;
    private String mobileNumber;
    private String email;
    private String address;
    private String city;
    private String state;

    public FirstModel() {

    }

    public FirstModel(String name, int age, String mobileNumber, String email, String address, String city, String state) {
        this.name = name;
        this.age = age;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
