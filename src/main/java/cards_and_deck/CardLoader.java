// temporaneo
// per caricare le carte dal file JSON
package cards_and_deck;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CardLoader {
    private final String jsonPath = "json/cards.json";
    private final Gson gson = new Gson();

    private JsonArray getArrayFromRoot(String key) throws Exception{
        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(jsonPath))) {

            JsonObject root = gson.fromJson(reader, JsonObject.class);
            return root.getAsJsonArray(key);
        }
    }

    public List<CharacterCard> loadCharacters() {
        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(jsonPath))) {
            Gson gson = new Gson();

            // Definiamo il tipo
            Type listType = new TypeToken<ArrayList<CharacterCard>>(){}.getType();

            // JSON legge il file e crea la lista
            return gson.fromJson(reader, listType);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<EventCard> loadEvents() {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();

            // Definiamo il tipo
            Type listType = new TypeToken<ArrayList<EventCard>>(){}.getType();

            // JSON legge il file e crea la lista
            return gson.fromJson(reader, listType);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<BuildingCard> loadBuildings() {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();

            // Definiamo il tipo
            Type listType = new TypeToken<ArrayList<BuildingCard>>(){}.getType();

            // JSON legge il file e crea la lista
            return gson.fromJson(reader, listType);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}