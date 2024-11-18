package dev3.projet.oxono_g63888.view;

import dev3.projet.oxono_g63888.model.ColorPawn;
import dev3.projet.oxono_g63888.model.Mark;
import dev3.projet.oxono_g63888.model.Pawn;
import dev3.projet.oxono_g63888.model.Totem;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class TokenGraphic extends StackPane {

        private Shape baseShape;
        private Shape topShape;
        private double size;

        public enum PieceType {
            PAWN, TOTEM
        }

        public TokenGraphic(Color color, PieceType type) {
            this.size = 40; // Ajustable en fonction du plateau

            // Effet d'ombre
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
            dropShadow.setColor(Color.color(0, 0, 0, 0.4));

            if (type == PieceType.TOTEM) {
                // Création du carré de base pour le Totem
                baseShape = new Rectangle(size, size);
                ((Rectangle) baseShape).setArcWidth(10); // Bords arrondis
                ((Rectangle) baseShape).setArcHeight(10);
                topShape = new Rectangle(size - 10, size - 10);
                ((Rectangle) topShape).setArcWidth(8);
                ((Rectangle) topShape).setArcHeight(8);
            } else if (type == PieceType.PAWN) {
                // Création du cercle de base pour le Pawn
                baseShape = new Circle(size / 2);
                topShape = new Circle(size / 2 - 5);
            }

            // Remplissage et effets
            LinearGradient baseGradient = new LinearGradient(
                    0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, color),
                    new Stop(1, color)
            );
            baseShape.setFill(baseGradient);
            baseShape.setEffect(dropShadow);
            topShape.setFill(color);
            topShape.setTranslateY(-3);

            getChildren().addAll(baseShape, topShape);
        }

        public void setMark(Mark mark) {
            getChildren().clear(); // Effacer les marquages précédents
            getChildren().addAll(baseShape, topShape);

            if (mark == Mark.X) {
                addMarkX();
            } else if (mark == Mark.O) {
                addMarkO();
            }
        }

        private void addMarkX() {
            // Lignes du 'X'
            Line line1 = new Line(-size / 4, -size / 4, size / 4, size / 4);
            Line line2 = new Line(-size / 4, size / 4, size / 4, -size / 4);
            line1.setStrokeWidth(4);
            line2.setStrokeWidth(4);
            line1.setStroke(Color.WHITE);
            line2.setStroke(Color.WHITE);

            // Effet d'ombre sur les lignes
            line1.setEffect(new DropShadow(2, 1, 1, Color.GRAY));
            line2.setEffect(new DropShadow(2, 1, 1, Color.GRAY));

            getChildren().addAll(line1, line2);
        }

        private void addMarkO() {
            // Cercle du 'O'
            if (baseShape instanceof Rectangle) {
                // Si c'est un Totem, utiliser un rectangle pour le 'O'
                Rectangle innerSquare = new Rectangle(size / 2, size / 2);
                innerSquare.setArcWidth(6);
                innerSquare.setArcHeight(6);
                innerSquare.setStroke(Color.WHITE);
                innerSquare.setStrokeWidth(4);
                innerSquare.setFill(Color.TRANSPARENT);
                innerSquare.setEffect(new DropShadow(2, 1, 1, Color.GRAY));

                getChildren().add(innerSquare);
            } else {
                // Si c'est un Pawn, utiliser un cercle pour le 'O'
                Circle circle = new Circle(size / 4);
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(4);
                circle.setFill(Color.TRANSPARENT);
                circle.setEffect(new DropShadow(2, 1, 1, Color.GRAY));

                getChildren().add(circle);
            }
        }


    public TokenGraphic createPawnFX(Pawn pawn) {
        Color color = pawn.getColor() == ColorPawn.PINK ? Color.DEEPPINK : Color.BLACK;
        TokenGraphic pawnFX = new TokenGraphic(color, PieceType.PAWN);
        pawnFX.setMark(pawn.getMark());
        return pawnFX;
    }

    /**
     * Crée un jeton visuel pour un totem
     */
    public TokenGraphic createTotemFX(Totem totem) {
        TokenGraphic totemFX = new TokenGraphic(Color.CYAN, PieceType.TOTEM);
        totemFX.setMark(totem.getMark());
        return totemFX;
    }
}