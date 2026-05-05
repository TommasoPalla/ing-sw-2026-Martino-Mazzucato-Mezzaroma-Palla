package it.polimi.ingsw.View.Listeners;

import it.polimi.ingsw.Model.GameBoard.OfferTile;

import java.util.ArrayList;
import java.util.HashMap;

public class ListenerManager {
    private final ArrayList<Listener> listeners;

    public ListenerManager(){
        this.listeners = new ArrayList<>();
    }

    public void addListener(Listener listener){
        if(listeners.contains(listener))
            return;
        listeners.add(listener);
    }
    public void removeListener(Listener listener){
        listeners.remove(listener);
    }

    public void notifyTurnChange(String player){
        for(Listener listener : listeners){
            listener.notifyTurnChange(player);
        }
    }

    public void notifyTotemPlaced(String player, OfferTile offerTile){
        for(Listener listener : listeners){
            listener.notifyTotemPlaced(player, offerTile);
        }
    }
}
