package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;
import it.polimi.ingsw.Model.Users.TribeInterface;

public class Builder extends CharacterCard {
    private final int prestigePoints;
    private final int buildingDiscount;

    public Builder(int era, String cardID, int numPlayersFlag, int prestigePoints, int buildingDiscount) {
        super(era, cardID, numPlayersFlag, CharacterRole.BUILDER);
        this.prestigePoints = prestigePoints;
        this.buildingDiscount = buildingDiscount;
    }
    public Builder(CharacterCardDTO characterData) {
        super(characterData.era, characterData.cardID, characterData.numPlayersFlag, CharacterRole.BUILDER);
        this.prestigePoints = characterData.prestigePoints;
        this.buildingDiscount = characterData.buildingDiscount;
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
    public void applyEffect(TribeInterface tribe) { tribe.addBuildersDiscount(buildingDiscount); }
}