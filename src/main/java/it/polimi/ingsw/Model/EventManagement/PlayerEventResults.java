package it.polimi.ingsw.Model.EventManagement;

import java.io.Serializable;

public record PlayerEventResults(String player, int[] foodAndPP) implements Serializable {}
