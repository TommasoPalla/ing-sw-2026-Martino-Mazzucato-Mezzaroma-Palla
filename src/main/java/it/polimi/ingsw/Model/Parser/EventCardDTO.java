package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.EventType;


public class EventCardDTO {
    public int era;
    public String cardID;
    public EventType eventType;
    public int artistThreshold;
    public int prestigeMalus;
    public int prestigeBonus;
    public int foodMalus;
    public int foodBonus;


    //used for testing
    public EventCardDTO(int era, String cardID, EventType eventType, int artistThreshold, int prestigeMalus, int prestigeBonus, int foodMalus, int foodBonus) {
        this.era = era;
        this.cardID = cardID;
        this.eventType = eventType;
        this.artistThreshold = artistThreshold;
        this.prestigeMalus = prestigeMalus;
        this.prestigeBonus = prestigeBonus;
        this.foodMalus = foodMalus;
        this.foodBonus = foodBonus;
    }
}