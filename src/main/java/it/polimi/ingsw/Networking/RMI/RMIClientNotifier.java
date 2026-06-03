package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.CustomException.StubException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.View.GamePlayers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RMIClientNotifier implements ClientNotifier {
    private final VirtualRMIClient clientStub;
    public RMIClientNotifier(VirtualRMIClient stub) {this.clientStub = stub;}

    @Override
    public void notifyGameCreated(int gameID, String playerName, int playerNum) {
        try {
            clientStub.updateGameCreated(gameID, playerNum);
        } catch (RemoteException e){
            //TODO: da sistemare tutte queste eccezioni
            //throw new StubException("could not notify");
        }
    }
    @Override
    public void notifyGameStarted(List<String> shuffledFirstPlayingOrder, Map<String,Integer> initialFood, ArrayList<Card> firstTopRow, ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow, ArrayList<BuildingCard> buildingsBottomRow) {
        try {
            clientStub.updateGameStarted(shuffledFirstPlayingOrder, initialFood, firstTopRow, firstBottomRow, buildingsTopRow, buildingsBottomRow);
        } catch (RemoteException e){}
    }

    @Override
    public void notifyNewPlayerConnected(String playerName) {
        try {
            clientStub.playerJoinedGame(playerName);
        } catch (RemoteException e){
            throw new StubException("could not notify new player");
        }
    }

    @Override
    public void notifySuccessfullyJoinedGame(int gameID, int playerNum, ArrayList<String> players, Map<String, Color> totemColors) {
        try {
            clientStub.successfullyJoinedGame(gameID, playerNum, players, totemColors);
        } catch (RemoteException e){
            throw new StubException("could not notify game created");
        }
    }

    @Override
    public void notifyPlayerLeftGame(String playerName) {
        try {
            clientStub.playerLeftGame(playerName);
        } catch (RemoteException e){
            throw new StubException("could not notify player left");
        }
    }

    @Override
    public void notifyNewHost() {
        try {
            clientStub.updateNewHost();
        } catch (RemoteException e){
            throw new StubException("could not notify new host");
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
            throw new StubException("could not notify totem color");
        }
    }

    @Override
    public void notifyStartRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults) {
        try {
            clientStub.startRound(lastEventsResults);
        } catch (RemoteException e) {
            throw new StubException("could not notify start of new round");
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
    public void notifyPassedTurn(String playerName) {
        try {
            clientStub.turnPassed(playerName);
        } catch (RemoteException e){

        }
    }

    @Override
    public void notifyChosenTile(String playerName, int index) {
        try {
            clientStub.chosenTile(playerName, index);
        } catch (RemoteException e) {
            throw new StubException("could not notify chosen tile");
        }
    }

    @Override
    public void notifyNewFood(String playerName, int newFoodReserve) {
        try {
            clientStub.updateFood(playerName, newFoodReserve);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyNewPrestigePoints(String playerName, int points) {
        try {
            clientStub.updatePp(playerName, points);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyNewShamansStars(String playerName, int stars) {
        try {
            clientStub.updateShamansStars(playerName, stars);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyNewBuildersDiscount(String playerName, int discount) {
        try {
            clientStub.updateBuildersDiscount(playerName, discount);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyNewGatherersDiscount(String playerName, int discount) {
        try {
            clientStub.updateGatherersDiscount(playerName, discount);
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

//    @Override
//    public void notifyNextPlayer(String playerName) {
//        try {
//            clientStub.updateNextPlayer(playerName);
//        } catch (RemoteException e) {
//            //throw new StubException("could not notify");
//        }
//    }

    @Override
    public void notifyGamePhase(GamePhase newPhase) {
        try {
            clientStub.updateGamePhase(newPhase);
        } catch (RemoteException e) {
            //throw new StubException("could not notify");
        }

    }

    @Override
    public void notifyEra(int era) {
        try {
            clientStub.updateEra(era);
        } catch (RemoteException e){
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyEndGame(Map<String, Integer> finalRanking) {
        try {
            clientStub.updateEndGame(finalRanking);
        } catch (RemoteException e){
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyLeaderboardInfo(List<String> leaderboard, int playerPosition) {
        try {
            clientStub.updateLeaderboardInfo(leaderboard, playerPosition);
        } catch (RemoteException e){
            //throw new StubException("could not notify");
        }
    }

    @Override
    public void notifyForceQuit(String disconnectedPlayer) {
        try {
            clientStub.forceQuit(disconnectedPlayer);
        } catch (RemoteException e){}
    }
}
