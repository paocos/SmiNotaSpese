package com.paocos.sminotaspese.model.entities.json;

import com.paocos.sminotaspese.model.entities.CardGps;
import com.paocos.sminotaspese.model.entities.CardOrari;
import com.paocos.sminotaspese.model.entities.CardPranzo;
import com.paocos.sminotaspese.model.entities.CardRiep;
import com.paocos.sminotaspese.model.entities.CardTemp;

/**
 * Created by paocos on 05/01/18.
 */

public class CardAllSync extends RspBase {

    private CardOrari cardOrari;
    private CardPranzo cardPranzo;
    private CardRiep cardRiep;

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
