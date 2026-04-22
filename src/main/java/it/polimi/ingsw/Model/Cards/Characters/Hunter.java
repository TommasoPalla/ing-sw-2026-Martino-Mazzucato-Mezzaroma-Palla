package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Users.Player;

public class Hunter extends CharacterCard {
    private final boolean hunterIcon;
    private final CharacterRole role;

    public Hunter(int era, String cardID, int numPlayersFlag,  boolean hunterIcon) {
        super(era, cardID, numPlayersFlag);
        this.hunterIcon = hunterIcon;
        this.role = CharacterRole.HUNTER;
    }

    // getters

    @Override
    public CharacterRole getRole() {
        return role;
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
