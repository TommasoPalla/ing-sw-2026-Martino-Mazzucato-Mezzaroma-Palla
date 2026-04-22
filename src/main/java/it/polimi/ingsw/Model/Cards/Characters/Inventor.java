package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Users.Player;

public class Inventor extends CharacterCard {
    private final InventorType invention;
    private final CharacterRole role;

    public Inventor(int era, String cardID, int numPlayersFlag, InventorType invention) {
        super(era, cardID, numPlayersFlag);
        this.invention = invention;
        this.role = CharacterRole.INVENTOR;
    }

    // getters
    @Override
    public CharacterRole getRole() {
        return role;
    }
    @Override
    public InventorType getInventorType() { return invention; }

    @Override
    public void applyEffect(Player player) {
        player.getTribe().getInventorsPerType().putIfAbsent(invention, 0);
        player.getTribe().getInventorsPerType().put(invention, player.getTribe().getInventorsPerType().get(invention) + 1);
    }
}
