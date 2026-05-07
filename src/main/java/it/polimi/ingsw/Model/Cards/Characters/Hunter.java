package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

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
    public void applyEffect(Player player) {
        if(hunterIcon) player.getTribe().addHunterFood(player.getTribe().getHuntersNumber());
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
