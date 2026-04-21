package cards_and_deck.characters;

import cards_and_deck.CharacterCard;
import enums.CharacterRole;

public class Artist extends CharacterCard {
    private final CharacterRole role;

    public Artist(int era, String cardID, int numPlayersFlag) {
        super(era, cardID, numPlayersFlag);
        this.role = CharacterRole.ARTIST;
    }

    @Override
    public CharacterRole getRole() {
        return role;
    }
}
