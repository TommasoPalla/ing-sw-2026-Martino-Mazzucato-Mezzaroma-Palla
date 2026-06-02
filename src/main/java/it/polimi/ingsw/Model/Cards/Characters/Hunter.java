package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;
import it.polimi.ingsw.Model.Users.TribeInterface;

import java.util.ArrayList;

/**
 * Whenever a player adds a Hunter without a Food icon to their tribe, they get nothing. Whenever they add a Hunter
 * with a Food icon to their tribe, they immediately take 1 Food token for each Hunter in their tribe (with or
 * without a Food icon).
 * During the Hunt Event, they take Food and gain Prestige Points based on the number of Hunters in their tribe.
 */
public class Hunter extends CharacterCard {
    private final boolean hunterIcon;

    public Hunter(int era, String cardID, int numPlayersFlag,  boolean hunterIcon) {
        super(era, cardID, numPlayersFlag, CharacterRole.HUNTER);
        this.hunterIcon = hunterIcon;
    }
    public Hunter(CharacterCardDTO characterData) {
        super(characterData.era, characterData.cardID, characterData.numPlayersFlag, CharacterRole.HUNTER);
        this.hunterIcon = characterData.hunterIcon;
    }

    @Override
    public boolean isAlphaHunter() {
        return hunterIcon;
    }

    @Override
    public void applyEffect(TribeInterface tribe) {
        if(hunterIcon) tribe.modifyFood(tribe.getHuntersNumber());
    }

    @Override
    public ArrayList<Object> getUsefulStats() {
        ArrayList<Object> usefulStats = new ArrayList<>();
        usefulStats.add("Era");
        usefulStats.add(getEra());
        usefulStats.add("Gives Food");
        usefulStats.add(hunterIcon);
        return usefulStats;
    }
}
