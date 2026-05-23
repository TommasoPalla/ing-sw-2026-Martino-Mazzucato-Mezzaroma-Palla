package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Parser.CharacterCardDTO;
import it.polimi.ingsw.Model.Users.TribeInterface;

public class Inventor extends CharacterCard {
    private final InventorType type;

    public Inventor(int era, String cardID, int numPlayersFlag, InventorType type) {
        super(era, cardID, numPlayersFlag, CharacterRole.INVENTOR);
        this.type = type;
    }
    public Inventor(CharacterCardDTO characterData) {
        super(characterData.era, characterData.cardID, characterData.numPlayersFlag, CharacterRole.INVENTOR);
        this.type = characterData.inventorType;
    }


    @Override
    public InventorType getInventorType() { return type; }

    @Override
    public void applyEffect(TribeInterface tribe) {
        tribe.addInventor(type);
    }
}
