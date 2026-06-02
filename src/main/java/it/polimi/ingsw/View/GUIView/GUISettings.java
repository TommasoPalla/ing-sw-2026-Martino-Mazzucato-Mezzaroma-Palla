package it.polimi.ingsw.View.GUIView;

/**
 * Utility class containing globally shared layout and dimension constants
 * for the graphical user interface components.
 * This class is final and cannot be instantiated.
 **/

public final class GUISettings {
    // Prevent instantiation
    private GUISettings() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final class Cards {
        public static final double WIDTH = 103.0;
        public static final double HEIGHT = 152.0;
    }

    public static final class Icons {
        public static final double ICON_WIDTH = 24.0;
        public static final double ICON_HEIGHT = 24.0;
        public static final double BIG_ICON_WIDTH = 48.0;
        public static final double BIG_ICON_HEIGHT = 48.0;
    }

    //da definire
    public static final class Totems {
        public static final double WIDTH = 24.0;
        public static final double HEIGHT = 24.0;
    }

    public static final class Tiles {
        public static final double WIDTH = 98;
        public static final double HEIGHT = 157;
    }

    public static final class PlayerWidget {
        public static final double WIDTH = 300;
        public static final double HEIGHT = 215;
    }
}