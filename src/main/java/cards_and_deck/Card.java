package cards_and_deck;

public abstract class Card {
    private final int era;
    private final String name;
    private final String cardID;

    public Card(int era, String name, String cardID){
      this.era = era;
      this.name = name;
      this.cardID = cardID;
    }

    public String getName(){
        return this.name;
    }
    public String getCardID(){
        return this.cardID;
    }
    public int getEra(){
        return this.era;
    }
}
