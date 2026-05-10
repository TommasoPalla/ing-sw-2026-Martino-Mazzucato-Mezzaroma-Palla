package it.polimi.ingsw.Model.Parser;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**CardLoader class manages methods that load cards from JSON file
 * it has a method for each type of cards*
 */
public class CardLoader {
    private final Gson gson = new Gson();

    /**
     *
     * @param key is the type of cards to extract from JSON (characters, buildings, events)
     * @return json Array to convert in a list of the specific type of card
     */
    private JsonArray getArrayFromRoot(String key, String path) {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path)))) {

            JsonElement rootElement = JsonParser.parseReader(reader);
            JsonObject root = rootElement.getAsJsonObject();
            return root.getAsJsonArray(key);
        }catch (IOException e) {
            System.err.println("JSON file loading failed "+e.getMessage());
            return new JsonArray();
        }
    }

    public List<CharacterCard> loadCharacters(String path) {
        CharacterRegistry characterRegistry = new CharacterRegistry();
        List<CharacterCard> allCharacterCards = new ArrayList<>();
        JsonArray array = getArrayFromRoot("characters", path);
        Type listType = new TypeToken<ArrayList<CharacterCardDTO>>() {
        }.getType();
        List<CharacterCardDTO> dtos = gson.fromJson(array, listType);
        if(dtos != null) {
            for(CharacterCardDTO dto : dtos){
                try {
                    allCharacterCards.add(characterRegistry.createCharacter(dto));
                }catch (InvocationTargetException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return allCharacterCards;
    }

    public List<EventCard> loadEvents(String path) {
        EventRegistry eventRegistry = new EventRegistry();
        List<EventCard> allEventCards = new ArrayList<>();
        JsonArray array = getArrayFromRoot("events", path);
        Type listType = new TypeToken<ArrayList<EventCardDTO>>() {
        }.getType();
        List<EventCardDTO> dtos = gson.fromJson(array, listType);
        if(dtos != null) {
            for(EventCardDTO dto : dtos){
                try {
                    allEventCards.add(eventRegistry.createEvent(dto));
                }catch (InvocationTargetException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return allEventCards;
    }

    public List<BuildingCard> loadBuildings(String path) {
        BuildingRegistry buildingRegistry = new BuildingRegistry();
        List<BuildingCard> allBuildingCards = new ArrayList<>();
        JsonArray array = getArrayFromRoot("buildings", path);
        Type listType = new TypeToken<ArrayList<BuildingCardDTO>>() {
        }.getType();
        List<BuildingCardDTO> dtos = gson.fromJson(array, listType);
        if(dtos != null) {
            for(BuildingCardDTO dto : dtos){
                try {
                    allBuildingCards.add(buildingRegistry.createBuilding(dto));
                }catch (InvocationTargetException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException e) {
                throw new RuntimeException(e);
                }
            }
        }
        return allBuildingCards;
    }
}