package enums;
import com.google.gson.annotations.SerializedName;

public enum Parameters {
    @SerializedName("F+")
    FOOD_BONUS,         //REPRESENTS A POSITIVE EFFECT e.g. DISCOUNT FOR SPECIFIC TYPE DURING SUSTENANCE

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
