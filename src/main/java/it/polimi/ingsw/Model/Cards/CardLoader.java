package it.polimi.ingsw.Model.Cards;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ArtistsFood;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCardDTO;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCardFactory;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingRegistry;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**CardLoader class manages methods that load cards from JSON file
 * it has a method for each type of cards*
 */
public class CardLoader {
    private final Gson gson = new Gson();

    public void setupCardsData() {
        //BuildingRegistry.addBuilding(Effect.ARTISTS_FOOD, ArtistsFood::new);
    }

    /**
     *
     * @param key is the type of cards to extract from JSON (characters, buildings, events)
     * @return json Array to convert in a list of the specific type of card
     * @throws Exception
     */
    private JsonArray getArrayFromRoot(String key, String path) throws Exception{
        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(path))) {

            JsonElement rootElement = JsonParser.parseReader(reader);
            JsonObject root = rootElement.getAsJsonObject();
            return root.getAsJsonArray(key);
        }
    }

    public List<CharacterCard> loadCharacters(String path) {
        try {
            JsonArray array = getArrayFromRoot("characters", path);
            // Definiamo il tipo
            Type listType = new TypeToken<ArrayList<CharacterCard>>(){}.getType();
            // JSON legge il file e crea la lista
            return gson.fromJson(array, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<EventCard> loadEvents(String path) {
        try {
            JsonArray array = getArrayFromRoot("events", path);
            // Definiamo il tipo
            Type listType = new TypeToken<ArrayList<EventCard>>(){}.getType();
            // JSON legge il file e crea la lista
            return gson.fromJson(array, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<BuildingCard> loadBuildings(String path) {
        List<BuildingCard> allBuildingCards = new ArrayList<>();

        try (Reader jsonReader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(path))) {
            //JsonArray array = getArrayFromRoot("buildings", path);
            // Definiamo il tipo
            JsonElement rootElement = JsonParser.parseReader(jsonReader);
            JsonObject root = rootElement.getAsJsonObject();
            JsonArray array =  root.getAsJsonArray("buildings");
            Type listType = new TypeToken<ArrayList<BuildingCardDTO>>() {
            }.getType();
            List<BuildingCardDTO> dtos = gson.fromJson(array, listType);
            if(dtos != null) {
                for(BuildingCardDTO dto : dtos){
                    allBuildingCards.add(BuildingCardFactory.createBuilding(dto));
                }
            }

        } catch (IOException e) {
            System.err.println("errore nel caricamento file JSon: "+e.getMessage());
            return new ArrayList<>();
        }
        return allBuildingCards;
    }
}