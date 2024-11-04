package g63888.ascii.view;

import g63888.ascii.model.*;

import java.util.List;

public class View {

    private final AsciiPaint paint;


    public View(AsciiPaint paint) {
        this.paint = paint;
    }

    /**
     * Displays the current state of the drawing on the console.
     * Each shape is rendered at its position using its color.
     */
    public void display() {
        for (int i = 0; i < paint.getWidth(); i++) {
            for (int j = 0; j < paint.getHeight(); j++) {
                System.out.print(paint.getColor(i, j));
            }
            System.out.println();
        }
    }

    /**
     * Displays a custom text message in the console.
     *
     * @param text the message to be displayed
     */
    public static void display(String text) {
        System.out.println(text);
    }

    /**
     * Displays the shapes currently present in the drawing.
     * Catches and displays any errors that occur during the operation.
     */
    public void showShapes() {
        try {
            display();
        } catch (Exception e) {
            display("An error occurred while showing shapes: " + e.getMessage());
        }
    }

    /**
     * Displays a list of shapes currently present in the drawing.
     * If the list is empty, it notifies the user that there are no shapes.
     */
    public void listShapes() {
        List<Shape> shapes = Drawing.getShapes();

        if (shapes.isEmpty()) {
            display("La Liste est vide.");
            return;
        }

        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            String shapeType = switch (shape) {
                case Circle circle -> "cercle";
                case Square square -> "carré";
                case Rectangle rectangle -> "rectangle";
                default -> "unknown shape";
            };
            display((i + 1) + ". " + shapeType);
        }
    }

    /**
     * Displays the menu of available commands for the user to interact with the application.
     * The commands include adding shapes, moving them, changing colors, deleting shapes, and quitting the application.
     */
    public void displayMenu() {
        display("Menu");
        display("");
        display("1. Ajouter un cercle : add circle 10 10 4 C");
        display("2. Ajouter un rectangle : add rectangle 10 10 4 6 R");
        display("3. Afficher les formes : show");
        display("4. Afficher la liste des shapes ; list");
        display("5. Déplacer une forme : move 2 10 6");
        display("6. Changer la couleur : color 4 C");
        display("7. Supprimer une forme : delete 1 (indice de la forme)");
        display("9. Grouper des formes : group 1 2 (indices des formes)");
        display("10. Dégrouper une forme : ungroup 1 (indice de la forme)");
        display("11. Annuler une action : undo");
        display("12. Rétablir une action : redo");
        display("13. Quitter : tapez 'Quit' pour quitter");
        display("");
    }
}
