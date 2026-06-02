package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.View.TUIView.TuiIcons;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This abstract class contains all the information common to all types of cards of the game.
 */
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

    public Map<String, String> getDisplayStats() {
        Map<String, String> stats = new LinkedHashMap<>();
        stats.put("ERA " + TuiIcons.ERA + ":", String.valueOf(era));
        return stats;
    }

    public abstract void accept(Visitor visitor);
}