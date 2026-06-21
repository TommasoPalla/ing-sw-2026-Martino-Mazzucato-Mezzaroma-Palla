package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Utils.Visitor;
import it.polimi.ingsw.View.TUIView.TuiIcons;

import java.util.Map;

/**
 * An Event card cannot be picked by players. It represents a certain {@link EventType} and it's resolved at the end of
 * the drawing phase before the start of the next round (if it's the last round, the top row events are resolved too).
 * It gives or takes Food tokens and/or Prestige Points from players based on the Event Type and the era of the event.
 * It extends the general class {@link Card}.
 */
public abstract class EventCard extends Card{

    private final EventType eventType;

    public EventCard(int era, String cardID, EventType eventType/*,
                     EnumMap<Parameters, Integer> inputPar*/){
        super(era, cardID);
        this.eventType = eventType;
    }

    /*
    + Getters
     */
    public EventType getEventType(){
        return this.eventType;
    }
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
        stats.put("EVENT " + TuiIcons.EVENT + ":", eventType.toIcon());
        return stats;
    }

    @Override
    public void accept(Visitor visitor){visitor.visitCard(this);}
}