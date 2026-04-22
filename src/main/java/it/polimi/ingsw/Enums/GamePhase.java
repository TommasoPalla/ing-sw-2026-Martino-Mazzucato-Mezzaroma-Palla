package it.polimi.ingsw.Enums;
/*
this enum represents all the possible game phases that could trigger the effect of a building, the activation of an event 
or the general administration of the game */
public enum GamePhase {
  ON_DRAW, 
  END_GAME, 
  ON_EVENT, 
  END_TURN,
  START_TURN,
  RETURN_TO_TILE,
  ON_PURCHASE;
}
