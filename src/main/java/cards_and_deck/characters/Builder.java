package cards_and_deck.characters;

import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import users.Player;

public class Builder extends CharacterCard {
    private final int prestigePoints;
    private final int buildingDiscount;
    private final CharacterRole role;

    public Builder(int era, String cardID, int numPlayersFlag, int prestigePoints, int buildingDiscount) {
        super(era, cardID, numPlayersFlag);
        this.prestigePoints = prestigePoints;
        this.buildingDiscount = buildingDiscount;
        this.role = CharacterRole.BUILDER;
    }

    // getters
    @Override
    public CharacterRole getRole() {
        return role;
    }
    @Override
    public int getPrestigePoints() {
        return prestigePoints;
    }
    @Override
    public int getBuildingDiscount() {
        return buildingDiscount;
    }

    @Override
    public void applyEffect(Player player) { player.getTribe().modifyBuildingDiscount(buildingDiscount); }
}
