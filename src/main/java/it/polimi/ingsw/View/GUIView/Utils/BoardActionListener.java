package it.polimi.ingsw.View.GUIView.Utils;

public interface BoardActionListener {
    public void onDrawCardRequested(boolean fromTopRow, boolean fromBuildings, int index);
    public void onPlaceTotemRequested(int index);
}
