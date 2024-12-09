package dev3.projet.oxono_g63888.view.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import dev3.projet.oxono_g63888.model.Position;
import java.util.List;
import java.util.Map;

public class GameAnimation {

    /**
     * Ajoute une animation de rebond pour les totems
     */
    public static void addTotemBounceAnimation(Node totem) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), totem);
        scaleUp.setFromX(0.3);
        scaleUp.setFromY(0.3);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        ScaleTransition scaleFinal = new ScaleTransition(Duration.millis(100), totem);
        scaleFinal.setFromX(1.2);
        scaleFinal.setFromY(1.2);
        scaleFinal.setToX(1.0);
        scaleFinal.setToY(1.0);

        RotateTransition rotate = new RotateTransition(Duration.millis(300), totem);
        rotate.setByAngle(360);

        ParallelTransition parallel = new ParallelTransition(scaleUp, rotate);
        SequentialTransition sequence = new SequentialTransition(parallel, scaleFinal);
        sequence.play();
    }

    /**
     * Ajoute une animation de chute pour les pions
     */
    public static void addPawnDropAnimation(Node pawn) {
        TranslateTransition drop = new TranslateTransition(Duration.millis(500), pawn);
        drop.setFromY(-100);
        drop.setToY(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), pawn);
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition parallel = new ParallelTransition(drop, scale);
        parallel.setInterpolator(Interpolator.EASE_OUT);
        parallel.play();
    }

    /**
     * Ajoute une animation de victoire
     */
    public static void addVictoryAnimation(List<Position> positions, Map<Position, StackPane> cellMap) {
        positions.forEach(pos -> {
            StackPane cell = cellMap.get(pos);
            if (cell != null) {
                // Animation de pulse
                ScaleTransition pulse = new ScaleTransition(Duration.millis(600), cell);
                pulse.setFromX(1.0);
                pulse.setFromY(1.0);
                pulse.setToX(1.2);
                pulse.setToY(1.2);
                pulse.setCycleCount(Timeline.INDEFINITE);
                pulse.setAutoReverse(true);

                // Animation de rotation
                RotateTransition rotate = new RotateTransition(Duration.millis(2000), cell);
                rotate.setByAngle(360);
                rotate.setCycleCount(Timeline.INDEFINITE);

                // Animation de brillance
                FadeTransition fade = new FadeTransition(Duration.millis(800), cell);
                fade.setFromValue(0.6);
                fade.setToValue(1.0);
                fade.setCycleCount(Timeline.INDEFINITE);
                fade.setAutoReverse(true);

                ParallelTransition parallel = new ParallelTransition(cell, pulse, rotate, fade);
                parallel.play();

                // Ajoute la classe de style pour l'effet de victoire
                cell.getStyleClass().add("victory-cell");
            }
        });
    }
}