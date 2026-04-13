package cards_and_deck;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**CardLoader class manages methods that load cards from JSON file
 * it has a method for each type of cards*
 */
public class CardLoader {
    private final Gson gson = new Gson();

    /**
     *
     * @param key is the type of cards to extract from JSON (characters, buildings, events)
     * @return json Array to convert in a list of the specific type of card
     * @throws Exception
     */
    private JsonArray getArrayFromRoot(String key) throws Exception{
        String jsonPath = "json/cards.json";
        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(jsonPath))) {

            JsonElement rootElement = JsonParser.parseReader(reader);
            JsonObject root = rootElement.getAsJsonObject();
            return root.getAsJsonArray(key);
        }
    }

    public List<CharacterCard> loadCharacters() {
        try {
            JsonArray array = getArrayFromRoot("characters");
            // Definiamo il tipo
            Type listType = new TypeToken<ArrayList<CharacterCard>>(){}.getType();
            // JSON legge il file e crea la lista
            return gson.fromJson(array, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<EventCard> loadEvents() {
        try {
            JsonArray array = getArrayFromRoot("events");
            // Definiamo il tipo
            Type listType = new TypeToken<ArrayList<EventCard>>(){}.getType();
            // JSON legge il file e crea la lista
            return gson.fromJson(array, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<BuildingCard> loadBuildings() {
        try {
            JsonArray array = getArrayFromRoot("buildings");
            // Definiamo il tipo
            Type listType = new TypeToken<ArrayList<BuildingCard>>() {
            }.getType();
            // JSON legge il file e crea la lista
            return gson.fromJson(array, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}