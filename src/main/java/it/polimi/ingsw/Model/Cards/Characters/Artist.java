package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;

public class Artist extends CharacterCard {
    public Artist(int era, String cardID, int numPlayersFlag) {
        super(era, cardID, numPlayersFlag, CharacterRole.ARTIST);
    }

    //constructor used by parser
    public Artist(CharacterCardDTO characterData) {
        super(characterData.era, characterData.cardID, characterData.numPlayersFlag, CharacterRole.ARTIST);
    }
}
