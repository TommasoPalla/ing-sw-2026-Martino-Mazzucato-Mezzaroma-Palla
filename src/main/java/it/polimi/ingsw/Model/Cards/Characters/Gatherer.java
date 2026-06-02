package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;
import it.polimi.ingsw.Model.Users.TribeInterface;

/**
 * During the Sustenance Event, gatherers provide a discount of Food tokens on the total the player would have to pay.
 */
public class Gatherer extends CharacterCard {
    private final int gathererDiscount;

    public Gatherer(int era, String cardID, int numPlayersFlag, int gathererDiscount) {
        super(era, cardID, numPlayersFlag, CharacterRole.GATHERER);
        this.gathererDiscount = gathererDiscount;
    }
    public Gatherer(CharacterCardDTO characterData) {
        super(characterData.era, characterData.cardID, characterData.numPlayersFlag, CharacterRole.GATHERER);
        this.gathererDiscount = characterData.gathererDiscount;
    }

    @Override
    public int getGathererDiscount() {
        return gathererDiscount;
    }

    @Override
    public void applyEffect(TribeInterface tribe) { tribe.addGathererDiscount(gathererDiscount); }
}
