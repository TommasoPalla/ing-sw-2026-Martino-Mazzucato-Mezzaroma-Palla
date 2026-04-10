package cards_and_deck;

import enums.*;
import java.util.EnumMap;


public class EventCard extends Card{
    private final EventType eventType;
    private final EnumMap<EventParam, Integer> parameters;

    /* nel file JSON verrano passati tutti gli attributi, della mappa solo i param
    che si vogliono, gli altri possono essere omessi*/
    public EventCard(int era, String name, String cardID, EventType eventType,
                     EnumMap<EventParam, Integer> inputPar){
        super(era, name, cardID);
        this.eventType = eventType;
        if(inputPar != null){
            this.parameters = new EnumMap<>(inputPar);
        }
        else {
            this.parameters = new EnumMap<>(EventParam.class);
        }

    }
    public EventType getEventType(){
        return this.eventType;
    }
    /* esempio di chiamata per chiedere solo foodBonus di evento caccia
    card.getParam(EventParam.FOOD_BONUS) */
    public int getParam(EventParam par){
        return parameters.getOrDefault(par, 0);
    }
}
