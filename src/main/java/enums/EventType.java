package enums;

import com.google.gson.annotations.SerializedName;
/*
this enum represents all the possible types of event*/

public enum EventType {
  @SerializedName("S")
  SUSTENANCE,

  @SerializedName("H")
  HUNT,

  @SerializedName("R")
  SHAMANIC_RITUAL,

  @SerializedName("C")
  CAVE_PAINTINGS
}
