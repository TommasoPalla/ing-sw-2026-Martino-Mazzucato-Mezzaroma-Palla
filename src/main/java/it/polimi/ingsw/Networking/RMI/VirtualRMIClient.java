package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface VirtualRMIClient extends Remote {
    void showUpdate() throws RemoteException;
    void reportError(String errorMessage) throws RemoteException;

    void updateGameCreated(int gameID, int numPlayers) throws RemoteException;
    void gameStarted(int gameID, int numPlayers) throws RemoteException;
    void playerJoinedGame(String playerName) throws RemoteException;
    void chosenTotem(String playerName, Color totemColor) throws RemoteException;
    void drawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) throws RemoteException;
    void chosenTile(String playerName, int index) throws RemoteException;

    void updateFood(String playerName, int food) throws RemoteException;
    void updateShamansStars(String playerName, int stars) throws RemoteException;
    void updatePrestigePoints(String playerName, int points) throws RemoteException;
    void updateTopRow(ArrayList<Card> newTopRow) throws RemoteException;
    void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings) throws RemoteException;
    void updateBottomRow(ArrayList<Card> newBottomRow) throws RemoteException;
    void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) throws RemoteException;
    void updateNextPlayer(String playerName) throws RemoteException;
    void updateGamePhase(GamePhase phase) throws RemoteException;
    void updateEra(int era) throws RemoteException;

}
