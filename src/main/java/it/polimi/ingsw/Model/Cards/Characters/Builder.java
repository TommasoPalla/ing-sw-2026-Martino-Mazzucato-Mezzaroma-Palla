package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Users.Player;

public class Builder extends CharacterCard {
    private final int prestigePoints;
    private final int buildingDiscount;

    public Builder(int era, String cardID, int numPlayersFlag, int prestigePoints, int buildingDiscount) {
        super(era, cardID, numPlayersFlag, CharacterRole.BUILDER);
        this.prestigePoints = prestigePoints;
        this.buildingDiscount = buildingDiscount;

    }

    // getters
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
