package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Users.Player;

public class Shaman extends CharacterCard {
    private final int shamansStars;
    private final CharacterRole role;

    public Shaman(int era, String cardID, int numPlayersFlag, int shamansStars) {
        super(era, cardID, numPlayersFlag, CharacterRole.SHAMAN);
        this.shamansStars = shamansStars;
        this.role = CharacterRole.SHAMAN;
    }

    // getters
    @Override
    public CharacterRole getRole() {
        return role;
    }
    @Override
    public int getShamansStars() {
        return shamansStars;
    }

    @Override
    public void applyEffect(Player player) { player.getTribe().addShamansStars(shamansStars); }
}
