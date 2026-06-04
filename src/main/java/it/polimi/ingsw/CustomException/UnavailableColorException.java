package it.polimi.ingsw.CustomException;

import it.polimi.ingsw.Enums.Color;

//goes under InvalidSelection
//TODO: throw the exception in the right method, lato model non va fatto, solo server
public class UnavailableColorException extends RuntimeException {
    public UnavailableColorException(Color color) {
        super(color.colorize(color.toString()) + " totem is already taken");
    }
}
