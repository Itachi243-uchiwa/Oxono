package dev3.projet.oxono_g63888.view.components;

import dev3.projet.oxono_g63888.model.Mark;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
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

    /**
     * Constructs a graphical representation of a game piece (TokenGraphic) with the specified color and type.
     * This graphical element is initialized with shapes and effects based on the piece type (PAWN or TOTEM).
     *
     * @param color The color to be applied to the graphical representation of the game piece.
     * @param type  The type of the game piece, determining whether it is represented as a circle (PAWN) or rounded rectangle (TOTEM).
     */
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

    /**
     * Updates the graphical representation of the token by clearing the existing visuals
     * and applying a new mark based on the specified type (Mark.X or Mark.O).
     *
     * @param mark The Mark to be displayed on the token. It can either be Mark.X or Mark.O,
     *             representing the type of token to draw on the graphical interface.
     */
    public void setMark(Mark mark) {
        getChildren().clear(); // Effacer les marquages précédents
        getChildren().addAll(baseShape, topShape);

        if (mark == Mark.X) {
            addMarkX();
        } else if (mark == Mark.O) {
            addMarkO();
        }
    }

    /**
     * Adds a graphical representation of an 'X' mark to this TokenGraphic.
     * This method creates two diagonal lines that intersect to form an 'X' shape.
     * The lines are styled with a white stroke color, a stroke width of 4, and a drop shadow effect
     * to provide a sense of depth. The 'X' is scaled relative to the token's size.
     * This graphical representation visually corresponds to the {@link Mark#X} enum value.
     *
     * The created lines are added to the children of this graphical component, ensuring
     * they are displayed on the screen when rendered within its parent container.
     */
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

    /**
     * Adds a graphical representation of a "O" mark to the token.
     *
     * This method creates and displays a "O" mark on the token, with its
     * appearance determined by the type of the base shape of the token:
     * - If the base shape is a rectangle (representing a totem), a rounded rectangle
     *   is used for the "O" mark.
     * - If the base shape is not a rectangle (representing a pawn), a circle is
     *   used for the "O" mark.
     *
     * The created shape is styled with the following properties:
     * - It has a white stroke color with a stroke width of 4.
     * - It is filled with a transparent color.
     * - It includes a drop shadow effect to enhance its visibility.
     */
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

}