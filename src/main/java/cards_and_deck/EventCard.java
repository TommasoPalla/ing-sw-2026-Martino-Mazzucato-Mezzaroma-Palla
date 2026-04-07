package cards_and_deck;

import enums.EventType;

public class EventCard extends Card {
    private final EventType type;

    //constructor
    EventCard(EventType type) { //rivedere perché da problemi
        this.type = type;
    }

    //getter method
    public EventType getType() { return type; }
}
