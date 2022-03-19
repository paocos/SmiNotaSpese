package com.paocos.sminotaspese.model.entities;

/**
 * Created by paocos on 05/01/18.
 */

public class CardTemp {

    private int temp;
    private int humidity;
    private int arrow; // es soglia a (12) 2 = <10 (alza) 1 = fra 10 e 11 alza 0 = ok -1 = fra 13 e 14 2 oltre 15

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getArrow() {
        return arrow;
    }

    public void setArrow(int arrow) {
        this.arrow = arrow;
    }
}
