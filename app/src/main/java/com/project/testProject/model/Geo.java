package com.project.testProject.model;

import com.google.gson.annotations.SerializedName;

public class Geo {

    @SerializedName("lng")
    private String lng;

    @SerializedName("lat")
    private String lat;

    public Geo(String lng, String lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return
                "Geo{" +
                        "lng = '" + lng + '\'' +
                        ",lat = '" + lat + '\'' +
                        "}";
    }
}