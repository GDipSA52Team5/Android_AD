package com.team5.splash;

import java.io.Serializable;

public class HawkerStall implements Serializable {

    // attributes
    private int id;
    private String stallName;
    private String unitNumber;
    private String contactNumber;
    private String[] tags;
    private String status;
    private String operatingHours;
    private String closeHours;
    private String stallImgUrl;

    // empty constructor
    public HawkerStall()
    {

    }

    // accessors

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public String getCloseHours() {
        return closeHours;
    }

    public void setCloseHours(String closeHours) {
        this.closeHours = closeHours;
    }

    public String getStallImgUrl() {
        return stallImgUrl;
    }

    public void setStallImgUrl(String stallImgUrl) {
        this.stallImgUrl = stallImgUrl;
    }
}
