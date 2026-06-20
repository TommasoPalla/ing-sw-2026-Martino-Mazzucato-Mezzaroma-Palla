package it.polimi.ingsw.CustomException;

import it.polimi.ingsw.Enums.Color;

public class UnavailableColorException extends RuntimeException {
    public UnavailableColorException(Color color) {
        super(color.colorize(color.toString()) + " totem is already taken");
    }
}
