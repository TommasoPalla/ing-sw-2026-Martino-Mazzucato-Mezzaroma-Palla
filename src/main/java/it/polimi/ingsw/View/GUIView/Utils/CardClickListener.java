package it.polimi.ingsw.View.GUIView.Utils;

@FunctionalInterface
public interface CardClickListener {
    void onCardClicked(boolean fromTopRow, boolean fromBuildings, int index);
}
