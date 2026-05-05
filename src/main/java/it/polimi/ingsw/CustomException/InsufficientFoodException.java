package it.polimi.ingsw.CustomException;

//goes under InvalidSelection
public class InsufficientFoodException extends RuntimeException {
    public InsufficientFoodException() {
        super("Not enough food to purchase");
    }
}
