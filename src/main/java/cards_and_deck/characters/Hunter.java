package cards_and_deck.characters;

import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import users.Player;

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
