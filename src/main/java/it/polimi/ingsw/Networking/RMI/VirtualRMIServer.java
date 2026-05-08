package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;
import it.polimi.ingsw.View.GamePlayers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * this interface has all the methods that need to be called from the client
 * (analogous of VirtualSocketServer)
 */

public interface VirtualRMIServer extends Remote {
    void connect(VirtualRMIClient clientStub) throws RemoteException;
    void disconnect(VirtualRMIClient clientStub) throws RemoteException;
    Map<Integer, GamePlayers> getActiveGames(VirtualRMIClient clientStub) throws RemoteException;
    void chooseOfferTile(VirtualRMIClient client, int index) throws OccupiedTileException, RemoteException;
    void drawCard(VirtualRMIClient client, boolean fromTopRow, boolean fromBuildings, int index) throws IllegalDrawException, RemoteException;
    void chooseTotemColor(VirtualRMIClient client, Color totemColor) throws  RemoteException;
    void joinGame(VirtualRMIClient client, String playerName, int gameID) throws  RemoteException;
    void leaveGame(String playerName, int gameID) throws RemoteException;
    void startGame(String playerName, int gameID) throws NotTheHostException, NotEnoughPlayersException, RemoteException;
    void createGame(VirtualRMIClient client, String playerName, int numPlayers) throws RemoteException;
    void chooseOfferTile(VirtualRMIClient client, PlayerRecord playerRecord, int index) throws RemoteException;
}
