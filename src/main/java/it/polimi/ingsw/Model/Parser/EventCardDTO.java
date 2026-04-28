package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.EventCard;


public class EventCardDTO {
    public int era;
    public String cardID;
    public EventType type;
    public int artistThreshold;
    public int prestigeMalus;
    public int prestigeBonus;
    public int foodMalus;
    public int foodBonus;
}
