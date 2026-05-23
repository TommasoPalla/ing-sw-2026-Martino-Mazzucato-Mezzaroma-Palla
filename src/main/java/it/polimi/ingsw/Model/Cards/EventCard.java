package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.Player;

import java.util.Map;


public abstract class EventCard extends Card{
    private final EventType eventType;

    //private final EnumMap<Parameters, Integer> parameters;

    /* nel file JSON verrano passati tutti gli attributi, della mappa solo i param
    che si vogliono, gli altri possono essere omessi*/
    public EventCard(int era, String cardID, EventType eventType/*,
                     EnumMap<Parameters, Integer> inputPar*/){
        super(era, cardID);
        this.eventType = eventType;
        /*if(inputPar != null){
            this.parameters = new EnumMap<>(inputPar);
        }
        else {
            this.parameters = new EnumMap<>(Parameters.class);
        }*/
    }
    public EventType getEventType(){
        return this.eventType;
    }
    /* esempio di chiamata per chiedere solo foodBonus di evento caccia
    card.getParam(EventParam.FOOD_BONUS) */
    /*public int getParam(Parameters par){
        return parameters.getOrDefault(par, 0);
    }*/
    public int getArtistThreshold(){
        return 0;
    }
    public int getPrestigeMalus(){
        return 0;
    }
    public int getPrestigeBonus(){
        return 0;
    }
    public int getFoodBonus(){
        return 0;
    }
    public int getFoodMalus(){
        return 0;
    }

    @Override
    public Map<String, String> getDisplayStats() {
        Map<String, String> stats = super.getDisplayStats();
        stats.put("🚩", eventType.toString());
        if (getFoodBonus() > 0) stats.put("🍖+", String.valueOf(getFoodBonus()));
        if (getFoodMalus() > 0) stats.put("🍖-", String.valueOf(getFoodMalus()));
        if (getPrestigeBonus() > 0) stats.put("⭐+", String.valueOf(getPrestigeBonus()));
        if (getPrestigeMalus() > 0) stats.put("⭐-", String.valueOf(getPrestigeMalus()));
        return stats;
    }

    @Override
    public void accept(Visitor visitor){visitor.visitCard(this);}
}