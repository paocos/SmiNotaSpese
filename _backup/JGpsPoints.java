package com.paocos.sminotaspese.model.entities.json;

import com.paocos.sminotaspese.model.entities.GpsPoint;

import java.util.ArrayList;

/**
 * Created by paocos on 05/06/17.
 */

public class JGpsPoints {

    private ArrayList<GpsPoint> gpsPoints;

    public JGpsPoints() {
    }

    public JGpsPoints(ArrayList<GpsPoint> gpsPoints) {
        this.gpsPoints = gpsPoints;
    }

    public ArrayList<GpsPoint> getGpsPoints() {
        return gpsPoints;
    }

    public void setGpsPoints(ArrayList<GpsPoint> gpsPoints) {
        this.gpsPoints = gpsPoints;
    }
}
