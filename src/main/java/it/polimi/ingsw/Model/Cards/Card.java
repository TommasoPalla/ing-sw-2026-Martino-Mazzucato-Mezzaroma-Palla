package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.Player;


public abstract class Card {
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
    public abstract void accept(Visitor visitor, Player player);
    public abstract void accept(Visitor visitor, OfferTrack offerTrack);
}
