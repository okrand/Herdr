package com.herdr.herdr;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Krando67 on 3/9/18.
 */

public class Herd {
    private String key;
    private String creatorID;
    private String title;
    private String description;
    private String time;
    private Date cal;
    private LatLon place;
    private String address;
    private List<String> subscriberID = Collections.emptyList();

    public String getKey() {
        return key;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public Date getCal() {
        return cal;
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

    public void setKey(String key) {
        this.key = key;
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

    public void setTime(String time) {
        this.time = time;
    }

    public void setCal(Date cal) {
        this.cal = cal;
    }

    public void setSubscriberID(List<String> subscriberID) {
        this.subscriberID = subscriberID;
    }
    public void addSubscriber(String newSubID){
        this.subscriberID.add(newSubID);
    }
}
