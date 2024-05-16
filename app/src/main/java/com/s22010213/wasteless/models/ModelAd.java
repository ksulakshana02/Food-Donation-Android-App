package com.s22010213.wasteless.models;

public class ModelAd {

    String id;
    String uid;
    String title;
    String description;
    String quantity;
    String cookedTime;
    String bestTime;
    String food_type;
    long timestamp;
    String address;
    double latitude;
    double longitude;

    public ModelAd() {
    }

    public ModelAd(String id, String uid, String title, String description, String quantity, String cookedTime, String bestTime, String food_type, long timestamp, String address, double latitude, double longitude) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.cookedTime = cookedTime;
        this.bestTime = bestTime;
        this.food_type = food_type;
        this.timestamp = timestamp;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCookedTime() {
        return cookedTime;
    }

    public void setCookedTime(String cookedTime) {
        this.cookedTime = cookedTime;
    }

    public String getBestTime() {
        return bestTime;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
