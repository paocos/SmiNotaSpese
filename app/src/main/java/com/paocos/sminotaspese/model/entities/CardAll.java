package com.paocos.sminotaspese.model.entities;

/**
 * Created by paocos on 05/01/18.
 */

public class CardAll {

    private CardGps cardGps;
    private CardTemp cardTemp;
    private CardOrari cardOrari;
    private CardPranzo cardPranzo;
    private CardRiep cardRiep;

    public CardGps getCardGps() {
        return cardGps;
    }

    public void setCardGps(CardGps cardGps) {
        this.cardGps = cardGps;
    }

    public CardTemp getCardTemp() {
        return cardTemp;
    }

    public void setCardTemp(CardTemp cardTemp) {
        this.cardTemp = cardTemp;
    }

    public CardOrari getCardOrari() {
        return cardOrari;
    }

    public void setCardOrari(CardOrari cardOrari) {
        this.cardOrari = cardOrari;
    }

    public CardPranzo getCardPranzo() {
        return cardPranzo;
    }

    public void setCardPranzo(CardPranzo cardPranzo) {
        this.cardPranzo = cardPranzo;
    }

    public CardRiep getCardRiep() {
        return cardRiep;
    }

    public void setCardRiep(CardRiep cardRiep) {
        this.cardRiep = cardRiep;
    }
}
