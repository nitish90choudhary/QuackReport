package com.example.quakereport;

import androidx.annotation.NonNull;

public class Earthquake {
    private String direction;
    private double magnitude;
    private String location;
    private String date;
    private String time;
    private String url;

    Earthquake() {
    }

    public Earthquake(float magnitude, String location, String date) {
        this.setDirection("hi dummy");
        this.setMagnitude(magnitude);
        this.setLocation(location);
        this.setDate(date);
        this.setTime("7:16 AM");
    }

    void setDate(String date) {
        this.date = date;
    }

    void setDirection(String direction) {
        this.direction = direction;
    }

    void setLocation(String location) {
        this.location = location;
    }

    void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    void setTime(String time) {
        this.time = time;
    }

    public String getDirection() {
        return direction;
    }

    public String getMagnitude() {
        return String.format("%.1f", magnitude);
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    @Override
    public String toString() {
        return ("Earthquake Object \n magnitude:" + this.getMagnitude() +
                "\n location: " + this.getLocation() +
                "\n direction: " + this.getDirection() +
                "\n url: " + this.getUrl() +
                "\n date: " + this.getDate() +
                "\n time: " + this.getTime());
    }
}
