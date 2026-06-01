package it.polimi.ingsw.Enums;

import com.google.gson.annotations.SerializedName;
import it.polimi.ingsw.View.TUIView.TuiIcons;
/**
 * This enum represents all the possible types of event that can occur during the game.
 */
public enum EventType {
  @SerializedName("S")
  SUSTENANCE(TuiIcons.GATHERER),

  @SerializedName("H")
  HUNT(TuiIcons.HUNTER),

  @SerializedName("R")
  SHAMANIC_RITUAL(TuiIcons.SHAMAN),

  @SerializedName("C")
  CAVE_PAINTINGS(TuiIcons.ARTIST);

  private final String icon;

  EventType(String icon){
    this.icon = icon;
  }

  public String toIcon(){
    return this.icon;
  }
}
