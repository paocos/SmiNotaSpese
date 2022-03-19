package com.paocos.sminotaspese.model.entities;

import java.sql.Timestamp;

/**
 * Created by paocos on 05/01/18.
 */

public class CardOrari {

    private Timestamp mattino_da;
    private Timestamp mattino_a;
    private Timestamp pomeriggio_da;
    private Timestamp pomeriggio_a;

    public Timestamp getMattino_da() {
        return mattino_da;
    }

    public void setMattino_da(Timestamp mattino_da) {
        this.mattino_da = mattino_da;
    }

    public Timestamp getMattino_a() {
        return mattino_a;
    }

    public void setMattino_a(Timestamp mattino_a) {
        this.mattino_a = mattino_a;
    }

    public Timestamp getPomeriggio_da() {
        return pomeriggio_da;
    }

    public void setPomeriggio_da(Timestamp pomeriggio_da) {
        this.pomeriggio_da = pomeriggio_da;
    }

    public Timestamp getPomeriggio_a() {
        return pomeriggio_a;
    }

    public void setPomeriggio_a(Timestamp pomeriggio_a) {
        this.pomeriggio_a = pomeriggio_a;
    }
}
