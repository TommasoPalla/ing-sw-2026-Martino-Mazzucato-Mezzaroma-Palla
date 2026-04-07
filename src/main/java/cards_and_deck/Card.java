package cards_and_deck;

import enums.Era;

public abstract class Card {
    private final Era era;
    private final String name;
    private final String cardID;

    public Card(Era era, String name, String cardID){
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
    public Era getEra(){
        return this.era;
    }
}
