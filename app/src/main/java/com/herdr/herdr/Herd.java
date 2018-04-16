package com.herdr.herdr;

import com.google.android.gms.location.places.Place;

import java.util.Collections;
import java.util.List;

/**
 * Created by Krando67 on 3/9/18.
 */

public class Herd {
    private String creatorID;
    private String title;
    private String description;
    private int today_tomorrow;
    private String time;
    private LatLon place;
    private String address;
    private List<String> subscriberID = Collections.emptyList();

    public String getCreatorID() {
        return creatorID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getToday_tomorrow() {
        return today_tomorrow;
    }

    public String getTime() {
        return time;
    }

    public LatLon getPlace() {
        return place;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getSubscriberID() {
        return subscriberID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlace(LatLon place) {
        this.place = place;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setToday_tomorrow(int today_tomorrow) {
        this.today_tomorrow = today_tomorrow;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSubscriberID(List<String> subscriberID) {
        this.subscriberID = subscriberID;
    }
}
