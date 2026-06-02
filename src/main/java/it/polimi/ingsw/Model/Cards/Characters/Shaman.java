package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;
import it.polimi.ingsw.Model.Users.TribeInterface;

/**
 * Shamans have a certain amount of Shamanic Stars shown on the card.
 * During the Shamanic Ritual Event, having the majority of these icons provides Prestige Points; having the minority,
 * on the other hand, results in losing Prestige Points.
 */
public class Shaman extends CharacterCard {
    private final int shamansStars;

    public Shaman(int era, String cardID, int numPlayersFlag, int shamansStars) {
        super(era, cardID, numPlayersFlag, CharacterRole.SHAMAN);
        this.shamansStars = shamansStars;
    }
    public Shaman(CharacterCardDTO characterData) {
        super(characterData.era, characterData.cardID, characterData.numPlayersFlag, CharacterRole.SHAMAN);
        this.shamansStars = characterData.shamanStars;
    }

    @Override
    public int getShamansStars() {
        return shamansStars;
    }

    @Override
    public void applyEffect(TribeInterface tribe) { tribe.addShamansStars(shamansStars); }
}
