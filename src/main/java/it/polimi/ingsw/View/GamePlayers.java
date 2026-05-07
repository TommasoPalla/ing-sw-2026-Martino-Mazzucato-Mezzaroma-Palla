package it.polimi.ingsw.View;

import java.io.Serializable;
import java.util.ArrayList;

public record GamePlayers(int playersNum, ArrayList<String> playerNames) implements Serializable {
}
