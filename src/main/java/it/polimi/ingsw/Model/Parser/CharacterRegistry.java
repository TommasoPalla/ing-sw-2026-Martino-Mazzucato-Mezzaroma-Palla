package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Cards.Characters.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

public class CharacterRegistry {
    private static final EnumMap<CharacterRole, Class<? extends CharacterCard>> registry = new EnumMap<>(CharacterRole.class);

    public CharacterRegistry(){
        registry.put(CharacterRole.ARTIST, Artist.class);
        registry.put(CharacterRole.BUILDER, Builder.class);
        registry.put(CharacterRole.GATHERER, Gatherer.class);
        registry.put(CharacterRole.HUNTER, Hunter.class);
        registry.put(CharacterRole.INVENTOR, Inventor.class);
        registry.put(CharacterRole.SHAMAN, Shaman.class);
    }
    //mainly used for testing and future implementation
    public void addCharacterType(CharacterRole role, Class<? extends CharacterCard> characterClass) {
        registry.put(role, characterClass);
    }

    public CharacterCard createCharacter(CharacterCardDTO characterData) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends CharacterCard> characterRole = registry.get(characterData.role);
        if(characterRole == null){
            throw new IllegalArgumentException("Character not identified");
        }
        Constructor<? extends CharacterCard> constructor = characterRole.getConstructor(CharacterCardDTO.class);
        return constructor.newInstance(characterData);
    }
}