package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;
import it.polimi.ingsw.Model.Users.Player;

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
}
