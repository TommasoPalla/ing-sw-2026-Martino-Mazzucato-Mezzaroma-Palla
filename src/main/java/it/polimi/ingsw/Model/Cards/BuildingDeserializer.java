package it.polimi.ingsw.Model.Cards;

import com.google.gson.*;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Model.Parser.BuildingRegistry;

import java.lang.reflect.Type;

/**
 * @deprecated
 */
public class BuildingDeserializer implements JsonDeserializer<BuildingCard> {
    BuildingRegistry buildingRegistry = new BuildingRegistry();
    private final Gson gson = new Gson();

    public BuildingCard deserialize(JsonElement json, Type buildingType,
                                    JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String effect = jsonObject.get("effect").getAsString();
        Effect effectType = Effect.valueOf(effect);// L'attributo del tuo JSON

        /*Class<? extends BuildingCard> targetClass = buildingRegistry.get();

        if (targetClass == null) {
            throw new JsonParseException("Tipo effetto non supportato: " + effectType);
        }
        //crea l'istanza di targetClass con i dati di json
        return context.deserialize(json, targetClass);*/
        return null;
    }
}
