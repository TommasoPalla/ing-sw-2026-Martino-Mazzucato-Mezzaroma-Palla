package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.EventCard;

//da cambiare con nuova gerarchia
public class EventCardDTO {
    private EventType type;
    public EventType getType(){
        return this.type;
    }
}
