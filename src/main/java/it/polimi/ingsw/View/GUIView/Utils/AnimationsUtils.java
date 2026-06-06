package it.polimi.ingsw.View.GUIView.Utils;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;


public class AnimationsUtils {
    public static void animateLabelUpdate(Label label, String newText) {
        // 1. Animazione di uscita: sbiadimento
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), label);
        fadeOut.setToValue(0.0);

        // Cambiamo il testo esattamente a metà, quando l'etichetta è invisibile
        fadeOut.setOnFinished(event -> label.setText(newText));

        // 2. Animazione di entrata: comparsa + leggero "pop" (effetto scala)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), label);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), label);
        scaleUp.setFromX(0.8);
        scaleUp.setFromY(0.8);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);

        // Eseguiamo fade in e scale up insieme
        ParallelTransition fadeInAndPop = new ParallelTransition(fadeIn, scaleUp);

        // Mettiamo tutto in sequenza: prima sparisce, poi riappare aggiornato
        SequentialTransition sequence = new SequentialTransition(fadeOut, fadeInAndPop);
        sequence.play();
    }
}