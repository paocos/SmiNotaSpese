package com.paocos.sminotaspese.model.entities;

/**
 * Created by paocos on 05/01/18.
 */

public class CardGps {

    private double km;
    private long time;
    private String whereIAm;

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWhereIAm() {
        return whereIAm;
    }

    public void setWhereIAm(String whereIAm) {
        this.whereIAm = whereIAm;
    }
}
