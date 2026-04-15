package enums;
import com.google.gson.annotations.SerializedName;



public enum Effect {
    @SerializedName("AF")
    ARTISTS_FOOD,

    @SerializedName("BP")
    BONUS_POINTS,

    @SerializedName("BS")
    BONUS_STARS,

    @SerializedName("CF")
    COMBO_FOOD,

    @SerializedName("CH")
    COMBO_HUNTERS,

    @SerializedName("CP")
    COMBO_POINTS,

    @SerializedName("DC")
    DRAW_CARD,

    @SerializedName("IF")
    INVENTORS_FOOD,

    @SerializedName("MR")
    MULTIPLIER_RITUAL,

    @SerializedName("MB")
    MULTIPLIER_BUILDER,

    @SerializedName("NR")
    NO_MALUS_RITUAL,

    @SerializedName("PR")
    POINTS_PER_ROLE,

    @SerializedName("SD")
    SUSTENANCE_DISCOUNT,

    @SerializedName("TF")
    TOTEM_FOOD
}
