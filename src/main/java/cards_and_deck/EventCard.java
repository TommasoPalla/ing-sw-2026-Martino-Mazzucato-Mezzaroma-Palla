package cards_and_deck;

import enums.EventType;
import enums.Era;

public class EventCard extends Card{
    private final EventType type;

    public EventCard(Era era, String name, String cardID, EventType type){
        super(era, name, cardID);
        this.type = type;
    }
    public EventType getType(){
        return this.type;
    }
}
