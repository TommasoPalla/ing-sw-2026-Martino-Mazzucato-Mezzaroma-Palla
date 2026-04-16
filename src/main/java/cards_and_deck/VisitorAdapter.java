package cards_and_deck;
/**
 * class VisitorAdapter overrides visit methods with blank bodies
 * to avoid further useless ones:
 * for each new Visitor object only the visit methods that are actually needed
 * will be re-defined
 */
public abstract class VisitorAdapter implements Visitor{
    @Override
    public void visitCard(BuildingCard building){}
    @Override
    public void visitCard(CharacterCard character){}
    @Override
    public void visitCard(EventCard event){}
}