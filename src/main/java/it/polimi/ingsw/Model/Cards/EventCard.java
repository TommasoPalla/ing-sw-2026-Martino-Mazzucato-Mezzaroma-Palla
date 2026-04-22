package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.Parameters;

import java.util.EnumMap;


public class EventCard extends Card{
    private final EventType eventType;
    private final EnumMap<Parameters, Integer> parameters;

    /* nel file JSON verrano passati tutti gli attributi, della mappa solo i param
    che si vogliono, gli altri possono essere omessi*/
    public EventCard(int era, String cardID, EventType eventType,
                     EnumMap<Parameters, Integer> inputPar){
        super(era, cardID);
        this.eventType = eventType;
        if(inputPar != null){
            this.parameters = new EnumMap<>(inputPar);
        }
        else {
            this.parameters = new EnumMap<>(Parameters.class);
        }

    }
    public EventType getEventType(){
        return this.eventType;
    }
    /* esempio di chiamata per chiedere solo foodBonus di evento caccia
    card.getParam(EventParam.FOOD_BONUS) */
    public int getParam(Parameters par){
        return parameters.getOrDefault(par, 0);
    }

    public void accept(Visitor visitor){
        visitor.visitCard(this);
    }
}
