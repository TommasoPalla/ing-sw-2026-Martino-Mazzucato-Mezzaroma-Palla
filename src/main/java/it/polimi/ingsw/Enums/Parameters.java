package it.polimi.ingsw.Enums;
import com.google.gson.annotations.SerializedName;

/**
 * This enum represents the possible type of parameters that can be passed by the effect context for an event resolving.
 */
public enum Parameters {
    @SerializedName("F+")
    FOOD_BONUS,

    @SerializedName("F-")
    FOOD_MALUS,         //ALWAYS POSITIVE. THE MINUS SIGN IS HANDLED INSIDE THE FUNCTIONS

    @SerializedName("P+")
    PRESTIGE_BONUS,

    @SerializedName("P-")
    PRESTIGE_MALUS,      //ALWAYS POSITIVE. THE MINUS SIGN IS HANDLED INSIDE THE FUNCTIONS

    @SerializedName("A")
    ARTIST_NUM           /*> THIS THRESHOLD, CAVE PAINTINGS GIVES PRESTIGE_BONUS FOR EACH ARTIST
                           <= THIS THRESHOLD, CAVE PAINTINGS GIVES PRESTIGE_MALUS */
}
