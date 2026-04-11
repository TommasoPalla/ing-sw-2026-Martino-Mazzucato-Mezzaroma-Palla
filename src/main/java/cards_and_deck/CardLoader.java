// temporaneo
// per caricare le carte dal file JSON
package cards_and_deck;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CardLoader {

    public List<CharacterCard> loadCharacters(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
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
    public List<EventCard> loadEvents(String filePath) {
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
}