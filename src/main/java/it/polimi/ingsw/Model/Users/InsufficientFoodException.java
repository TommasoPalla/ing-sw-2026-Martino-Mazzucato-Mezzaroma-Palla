package it.polimi.ingsw.Model.Users;

public class InsufficientFoodException extends RuntimeException {
    public InsufficientFoodException() {
        super("Not enough food to purchase");
    }
}
