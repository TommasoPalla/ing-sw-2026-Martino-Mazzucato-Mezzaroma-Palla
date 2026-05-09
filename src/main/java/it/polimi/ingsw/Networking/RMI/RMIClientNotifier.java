package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.CustomException.StubException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.View.GamePlayers;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class RMIClientNotifier implements ClientNotifier {
    private final VirtualRMIClient clientStub;
    public RMIClientNotifier(VirtualRMIClient stub) {this.clientStub = stub;}

    @Override
    public void notifyGameCreated(int gameID, int playerNum) {
        try {
            clientStub.updateGameCreated(gameID, playerNum);
        } catch (RemoteException e){
            //TODO: da sistemare tutte queste eccezioni
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyNewPlayerConnected(String playerName) {
        try {
            clientStub.playerJoinedGame(playerName);
        } catch (RemoteException e){
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifySuccessfullyJoinedGame(int gameID, int playerNum, ArrayList<String> players) {
        try {
            clientStub.successfullyJoinedGame(gameID, playerNum, players);
        } catch (RemoteException e){
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyPlayerLeftGame(String playerName) {
        try {
            clientStub.playerLeftGame(playerName);
        } catch (RemoteException e){
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyAvailableGames(Map<Integer, GamePlayers> availableGames){
        try {
            clientStub.updateAvailableGames(availableGames);
        } catch (RemoteException e){
            throw new StubException("could not notify active games");
        }
    }

    @Override
    public void notifyTotemColor(String playerName, Color totemColor) {
        try {
            clientStub.chosenTotem(playerName, totemColor);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        try {
            clientStub.drawnCard(playerName, fromTopRow, fromBuildings, index);
        } catch (RemoteException e){
            //throw new StubException("could not notify");
        }
    }


    @Override
    public void notifyChosenTile(String playerName, int index) {
        try {
            clientStub.chosenTile(playerName, index);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyFoodToAdd(String playerName, int food) {
        try {
            clientStub.chosenTile(playerName, food);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyShamansStarsToAdd(String playerName, int food) {
        try {
            clientStub.updateShamansStars(playerName, food);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyPrestigePointsToAdd(String playerName, int points) {
        try {
            clientStub.updatePrestigePoints(playerName, points);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyTopRow(ArrayList<Card> newTopRow) {
        try {
            clientStub.updateTopRow(newTopRow);
        } catch (Exception e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyTopBuildings(ArrayList<BuildingCard> newTopBuildings) {
        try {
            clientStub.updateTopBuildings(newTopBuildings);
        } catch (Exception e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyBottomRow(ArrayList<Card> newBottomRow) {
        try {
            clientStub.updateBottomRow(newBottomRow);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) {
        try {
            clientStub.updateBottomBuildings(newBottomBuildings);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyNextPlayer(String playerName) {

    }

    @Override
    public void notifyGamePhase(GamePhase newPhase) {

    }

    @Override
    public void notifyEra(int era) {

    }
}
