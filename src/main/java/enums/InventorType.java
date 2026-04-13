package enums;

import com.google.gson.annotations.SerializedName;
/*
this enum represents all the possible types related to the inventor character*/
public enum InventorType {
  @SerializedName("B")
  BOAT,

  @SerializedName("A")
  ARROW,

  @SerializedName("H")
  HOOK,

  @SerializedName("N")
  NECKLACE,

  @SerializedName("W")
  BOWL,

  @SerializedName("R")
  ROPE,

  @SerializedName("D")
  DOLL,

  @SerializedName("F")
  FLUTE,

  @SerializedName("L")
  LEATHER,

  @SerializedName("E")
  BREAD,

  NONE
}
