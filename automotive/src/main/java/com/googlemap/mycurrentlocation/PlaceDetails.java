package com.googlemap.mycurrentlocation;

public class PlaceDetails {
    private String name;
    private String address;
    private String phoneNumber;
    private double rating;

    public PlaceDetails(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public PlaceDetails(String name, String address, double rating) {
        this.name = name;
        this.address = address;
        this.rating = rating;
    }
    // Constructor
    public PlaceDetails(String name, String address, String phoneNumber, double rating) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getRating() {
        return rating;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}