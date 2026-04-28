package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

public class CharacterRegistry {
    private static final EnumMap<CharacterRole, Class<? extends CharacterCard>> registry = new EnumMap<>(CharacterRole.class);
    public CharacterCard createCharacter(CharacterCardDTO characterData) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends CharacterCard> characterRole = registry.get(characterData.role);
        if(characterRole == null){
            throw new IllegalArgumentException("Character not identified");
        }
        Constructor<? extends CharacterCard> constructor = characterRole.getConstructor(CharacterCardDTO.class);
        return constructor.newInstance(characterData);
    }
}