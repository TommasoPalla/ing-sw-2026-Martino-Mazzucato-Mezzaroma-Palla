package cards_and_deck;

/**
 * Visitor interface uses visitor Pattern in order to let
 * room for future additions of new methods, used by the three types
 * of cards but in different ways.
 */
public interface Visitor {
    void visitBuilding(BuildingCard building);
    void visitCharacter(CharacterCard character);
    void visitEvent(EventCard event);
}
