package com.paocos.sminotaspese.model.entities;

/**
 * Created by paocos on 12/03/17.
 */

public class GpsPoint {

    private Double latitudine;
    private Double longitudine;
    private Double altitudine;
    private Double velocity;
    private Double distance;
    private long timeInterval;
    private int nrOfFixSatellites;
    private int timeToFix;
    private double accuracy;

    public Double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(Double latitudine) {
        this.latitudine = latitudine;
    }

    public Double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(Double longitudine) {
        this.longitudine = longitudine;
    }

    public Double getAltitudine() {
        return altitudine;
    }

    public void setAltitudine(Double altitudine) {
        this.altitudine = altitudine;
    }

    public Double getVelocity() {
        return velocity;
    }

    public void setVelocity(Double velocity) {
        this.velocity = velocity;
    }

    public int getNrOfFixSatellites() {
        return nrOfFixSatellites;
    }

    public void setNrOfFixSatellites(int nrOfFixSatellites) {
        this.nrOfFixSatellites = nrOfFixSatellites;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public int getTimeToFix() {
        return timeToFix;
    }

    public void setTimeToFix(int timeToFix) {
        this.timeToFix = timeToFix;
    }
}
