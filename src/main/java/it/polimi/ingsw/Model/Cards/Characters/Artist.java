package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;

public class Artist extends CharacterCard {
    private final CharacterRole role;

    public Artist(int era, String cardID, int numPlayersFlag) {
        super(era, cardID, numPlayersFlag, CharacterRole.ARTIST);
        this.role = CharacterRole.ARTIST;
    }

    @Override
    public CharacterRole getRole() {
        return role;
    }
}
