package it.polimi.ingsw.Enums;

import com.google.gson.annotations.SerializedName;
import it.polimi.ingsw.View.TUIView.TuiIcons;

/**
 * This enum represents all the possible roles of a character.
 */
public enum CharacterRole {
    @SerializedName("H")
    HUNTER(TuiIcons.HUNTER),

    @SerializedName("B")
    BUILDER(TuiIcons.BUILDER),

    @SerializedName("G")
    GATHERER(TuiIcons.GATHERER),

    @SerializedName("A")
    ARTIST(TuiIcons.ARTIST),

    @SerializedName("S")
    SHAMAN(TuiIcons.SHAMAN),

    @SerializedName("I")
    INVENTOR(TuiIcons.INVENTOR),

    NONE("");

    /**
     * The {@link TuiIcons} icon associated to the role.
     */
    private final String icon;

    CharacterRole(String icon){
        this.icon = icon;
    }

    public String toIcon(){
        return this.icon;
    }
}