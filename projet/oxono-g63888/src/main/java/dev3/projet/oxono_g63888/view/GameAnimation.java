package dev3.projet.oxono_g63888.view;

import dev3.projet.oxono_g63888.model.Position;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class GameAnimation {
    public static void addTotemBounceAnimation(Node totemFx) {

        ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(0.3), totemFx);
        scaleUp.setFromX(1);
        scaleUp.setFromY(1);
        scaleUp.setToX(1.5);
        scaleUp.setToY(1.5);

        ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.3), totemFx);
        scaleDown.setFromX(1.5);
        scaleDown.setFromY(1.5);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        SequentialTransition animation = new SequentialTransition(scaleUp, scaleDown);
        animation.play();


    }

    public static void addPawnDropAnimation(Node pawnFx) {
        TranslateTransition translate = new TranslateTransition(Duration.seconds(0.4), pawnFx);
        translate.setFromY(-20);
        translate.setToY(0);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.4), pawnFx);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition animation = new ParallelTransition(translate, fade);
        animation.play();
    }
    public static void addVictoryAnimation(List<Position> positions, Map<Position, StackPane> cellMap) {
        // Appliquer l'animation à chaque position
        for (Position pos : positions) {
            StackPane newCell = cellMap.get(pos);

            if (newCell == null) {
                System.err.println("Erreur : Aucun StackPane trouvé pour la position " + pos);
                continue;
            }

            ObservableList<Node> children = newCell.getChildren();
            if (children.isEmpty()) {
                System.err.println("Erreur : Aucun enfant dans le StackPane pour la position " + pos);
                continue;
            }

            Node pawnWin = children.get(0); // Accéder au premier enfant
            addSpinGlowAnimation(pawnWin);
        }
    }



    public static void addSpinGlowAnimation(Node totemFx) {
        // Transition de rotation
        RotateTransition rotate = new RotateTransition(Duration.seconds(1), totemFx);
        rotate.setByAngle(360);  // Effectue une rotation complète de 360°

        // Transition de fondu
        FadeTransition fade = new FadeTransition(Duration.seconds(1), totemFx);
        fade.setFromValue(0.5);  // Commence avec une transparence de 0.5
        fade.setToValue(1);      // Termine avec une pleine opacité

        // Ajout de l'effet de lumière (ombre portée)
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.YELLOW);  // Choix d'une couleur vive pour l'ombre (ici jaune)
        shadow.setRadius(20);           // Rayon de l'ombre
        totemFx.setEffect(shadow);

        // Lancer les animations en parallèle
        ParallelTransition animation = new ParallelTransition(rotate, fade);
        animation.setCycleCount(10);  // L'animation se joue une fois
        animation.play();           // Lance l'animation
    }


}