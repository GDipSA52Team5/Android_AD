package com.team5.splash.models;

import java.io.Serializable;

public class HawkerCentre implements Serializable {

    // attributes
    private String id;
    private String name;
    private String address;
    private int numOfStalls;
    private double latitude;
    private double longitude;
    private String imgUrl;

    // empty constructor
    public HawkerCentre()
    { }

    // accessors
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumOfStalls() {
        return numOfStalls;
    }

    public void setNumOfStalls(int numOfStalls) {
        this.numOfStalls = numOfStalls;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
