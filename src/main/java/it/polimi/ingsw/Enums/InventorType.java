package it.polimi.ingsw.Enums;

import com.google.gson.annotations.SerializedName;
import it.polimi.ingsw.View.TUIView.TuiIcons;

/*
this enum represents all the possible types related to the inventor character*/
public enum InventorType {
  @SerializedName("B")
  BOAT(TuiIcons.BOAT_INVENTOR),

  @SerializedName("A")
  ARROW(TuiIcons.ARROW_INVENTOR),

  @SerializedName("H")
  HOOK(TuiIcons.HOOK_INVENTOR),

  @SerializedName("N")
  NECKLACE(TuiIcons.NECKLACE_INVENTOR),

  @SerializedName("W")
  BOWL(TuiIcons.BOWL_INVENTOR),

  @SerializedName("R")
  ROPE(TuiIcons.ROPE_INVENTOR),

  @SerializedName("D")
  DOLL(TuiIcons.DOLL_INVENTOR),

  @SerializedName("F")
  FLUTE(TuiIcons.FLUTE_INVENTOR),

  @SerializedName("L")
  LEATHER(TuiIcons.LEATHER_INVENTOR),

  @SerializedName("E")
  BREAD(TuiIcons.BREAD_INVENTOR),

  NONE("");

  private final String icon;

  InventorType(String icon){
    this.icon = icon;
  }

  public String toIcon(){
    return this.icon;
  }
}