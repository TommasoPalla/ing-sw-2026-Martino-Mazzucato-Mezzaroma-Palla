package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Users.Player;

public class Gatherer extends CharacterCard {
    private final int gathererDiscount;
    private final CharacterRole role;

    public Gatherer(int era, String cardID, int numPlayersFlag, int gathererDiscount) {
        super(era, cardID, numPlayersFlag);
        this.gathererDiscount = gathererDiscount;
        this.role = CharacterRole.GATHERER;
    }

    @Override
    public CharacterRole getRole() {
        return role;
    }

    @Override
    public void applyEffect(Player player) { player.getTribe().modifyGatherersDiscount(gathererDiscount); }
}
