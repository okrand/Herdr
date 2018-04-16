package com.herdr.herdr;

public class LatLon {
    private Double latitude;
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LatLon() {}
    public LatLon(Double lat, Double lon){latitude = lat; longitude = lon;}
}
