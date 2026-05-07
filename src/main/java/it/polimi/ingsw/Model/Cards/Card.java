package it.polimi.ingsw.Model.Cards;

import java.io.Serializable;


public abstract class Card implements Serializable {
    private final int era;
    private final String cardID;

    public Card(int era, String cardID){
      this.era = era;
      this.cardID = cardID;
    }

    public String getCardID(){
        return this.cardID;
    }
    public int getEra(){
        return this.era;
    }
    public abstract void accept(Visitor visitor);
}