package enums;

import com.google.gson.annotations.SerializedName;
/*
this enum represents all the possible roles of a character*/

public enum CharacterRole {
  @SerializedName("H")
  HUNTER,

  @SerializedName("B")
  BUILDER,

  @SerializedName("G")
  GATHERER,

  @SerializedName("A")
  ARTIST,

  @SerializedName("S")
  SHAMAN,

  @SerializedName("I")
  INVENTOR,

  NONE
}
