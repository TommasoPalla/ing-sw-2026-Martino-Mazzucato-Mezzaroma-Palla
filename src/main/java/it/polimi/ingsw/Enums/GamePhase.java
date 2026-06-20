package it.polimi.ingsw.Enums;
/**
 * This enum represents all the possible game phases that could trigger the effect of a building, the activation of an event
 * or the general administration of the game.
 */
public enum GamePhase {
  START_GAME,
  START_TURN,
  ON_DRAW,
  ON_PURCHASE,
  RETURN_TO_TILE,
  ON_EVENT, 
  END_TURN,
  END_GAME
}
