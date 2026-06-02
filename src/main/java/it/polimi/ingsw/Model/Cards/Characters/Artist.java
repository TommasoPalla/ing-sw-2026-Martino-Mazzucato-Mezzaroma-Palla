package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;

/**
 * During the Cave Paintings Event, the player can gain or lose Prestige Points based on the number of Artist in their
 * tribe.
 * At the end of the game, they gain 10 Prestige Points for every 2 artists in their tribe.
 */
public class Artist extends CharacterCard {
    public Artist(int era, String cardID, int numPlayersFlag) {
        super(era, cardID, numPlayersFlag, CharacterRole.ARTIST);
    }

    //constructor used by parser
    public Artist(CharacterCardDTO characterData) {
        super(characterData.era, characterData.cardID, characterData.numPlayersFlag, CharacterRole.ARTIST);
    }
}
