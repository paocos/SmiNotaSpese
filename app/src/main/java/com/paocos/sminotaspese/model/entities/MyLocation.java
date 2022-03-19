package com.paocos.sminotaspese.model.entities;

import android.location.Address;
import android.location.Location;

/**
 * Created by paocos on 16/03/17.
 */

public class MyLocation extends Location {

    private double distance;
    private Address address;
    private double mySpeed;
    private double myAltitude;
    private double myLatitude;
    private double myLongitude;
    private float myTime;
    private int timeToFix;
    private int nrOfSatellites;

    public MyLocation(Location l) {
        super(l);
        set(l);
    }

    public MyLocation(MyLocation l) {
        super(l);
        set(l);
    }

    public void set(MyLocation l) {
        this.setDistance(l.getDistance());
        this.setAddress(l.getAddress());
        this.setMySpeed(l.getMySpeed());
        this.setMyTime(l.getMyTime());
        this.setTimeToFix(l.getTimeToFix());
        this.setNrOfSatellites(l.getNrOfSatellites());
        this.setMyLatitude(l.getMyLatitude());
        this.setMyLongitude(l.getMyLongitude());
        this.setMyAltitude(l.getMyAltitude());
    }

    public int getNrOfSatellites() {
        return nrOfSatellites;
    }

    public void setNrOfSatellites(int nrOfSatellites) {
        this.nrOfSatellites = nrOfSatellites;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public double getMySpeed() {
        return mySpeed;
    }

    public void setMySpeed(double mySpeed) {
        this.mySpeed = mySpeed;
    }

    public int getTimeToFix() {
        return timeToFix;
    }

    public void setTimeToFix(int timeToFix) {
        this.timeToFix = timeToFix;
    }

    public float getMyTime() {
        return myTime;
    }

    public void setMyTime(float myTime) {
        this.myTime = myTime;
    }

    public double getMyAltitude() {
        return myAltitude;
    }

    public void setMyAltitude(double myAltitude) {
        this.myAltitude = myAltitude;
    }

    public double getMyLatitude() {
        return myLatitude;
    }

    public void setMyLatitude(double myLatitude) {
        this.myLatitude = myLatitude;
    }

    public double getMyLongitude() {
        return myLongitude;
    }

    public void setMyLongitude(double myLongitude) {
        this.myLongitude = myLongitude;
    }
}
