package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.ClientController;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.LightTribe;

import java.util.ArrayList;

public class TUIView {
    final private ClientModel localModel;
    final private ClientController clientController;
    private final String player;

    public TUIView(ClientModel localModel, ClientController clientController, String player) {
        this.localModel = localModel;
        this.clientController = clientController;
        this.player = player;

    }

    /**
     * Prints to terminal the tribe of the player
     */
    private void printPersonalTribe() {
        System.out.println("This is your tribe:\n");
        LightTribe localTribe = localModel.getPlayerTribe(player);
        if(localTribe != null) {
            System.out.println("Food Reserve: " + localTribe.getFoodReserve());
            System.out.println("\n");
            System.out.println("Prestige Points: " + localTribe.getPrestigePoints());
            System.out.println("\n");
            for(CharacterRole role : CharacterRole.values()) {
                if(localTribe.getPopulation().get(role).isEmpty()) continue;
                System.out.println(role + ": ");
                //printRoleCardsInPopulation();
                System.out.println("\n");
            }
        }
    }

    /**
     * Prints to terminal a list of cards of a specific role present in a player's tribe.
     * @param playerTribe the reference to the player's tribe whose cards want to be visualised.
     * @param role the role of the character cards which want to be visualised.
     */
    private void printRoleCardsInPopulation(LightTribe playerTribe, CharacterRole role) {
        ArrayList<CharacterCard> cards = playerTribe.getPopulation().get(role);
        for(CharacterCard card : cards) {
            // bisogna trovare un modo per stampare le carte anche in base al loro ruolo, possibilmente senza switch
            System.out.println("\n");
        }
    }

    /**
     * Prints to terminal the top row of the offerTrack
     */
    private void printTopRow() {
        if(localModel.getTopRow().isEmpty()) {
            System.out.println("La fila superiore è vuota");
            return;
        }
        // TopRowTUIView();
    }

    /**
     * Prints to terminal the bottom row of the offerTrack
     */
    private void printBottomRow() {
        if(localModel.getBottomRow().isEmpty()) {
            System.out.println("La fila inferiore è vuota");
            return;
        }
        // TopRowTUIView();
    }

    private void printOfferTrack() {
        for (OfferTile offerTile : localModel.getOfferTiles()) {
            System.out.println("______ ");
        }
        System.out.println("\n");
        System.out.println("|\n ");
        for (OfferTile offerTile : localModel.getOfferTiles()) {
                System.out.println("Food: " + offerTile.getFoodBonus() + ", ");
                System.out.println("^ " + offerTile.getCardsFromAbove() + ", ");
                System.out.println("v " + offerTile.getCardsFromBelow());
                System.out.println(" | ");
        }
        System.out.println("\n| ");
        for (OfferTile offerTile : localModel.getOfferTiles()) {
            if(offerTile.isOccupied()) {
                System.out.println(offerTile.getCurrentOccupant().getName());
            }
            else  {
                System.out.println("---");
            }
        }
        //da finire, e da modificare perché ho sbagliato, meglio rappresentazione verticale dell'offerTrack
    }
}
