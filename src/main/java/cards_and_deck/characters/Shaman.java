package cards_and_deck.characters;

import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import users.Player;

public class Shaman extends CharacterCard {
    private final int shamansStars;
    private final CharacterRole role;

    public Shaman(int era, String cardID, int numPlayersFlag, int shamansStars) {
        super(era, cardID, numPlayersFlag);
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
