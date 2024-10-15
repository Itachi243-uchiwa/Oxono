package g63888.ascii.model;

import g63888.ascii.controller.CommandPattern;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import g63888.ascii.view.View;

public class AsciiPaint {

    private final Drawing drawing;


    public AsciiPaint(int width, int height) {
        this.drawing = new Drawing(width, height);
    }

    /**
     * Adds a circle to the drawing based on a command string.
     * The command is expected to match the pattern for adding circles.
     *
     * @param command the command string to add a circle (e.g., "add circle 10 10 5 r")
     */
    public void addCircle(String command) {
            Matcher matcher = Pattern.compile(CommandPattern.ADD_CIRCLE.getPattern()).matcher(command);
            if (matcher.find()) {
                double centerX = Double.parseDouble(matcher.group(1));
                double centerY = Double.parseDouble(matcher.group(2));
                double radius = Double.parseDouble(matcher.group(3));
                char color = matcher.group(4).charAt(0);
                drawing.addShape(new Circle(new Point(centerX, centerY), radius, color));
                View.display("Circle added.");
            } else {
                View.display("Invalid circle command.");
            }

    }

    /**
     * Adds a rectangle to the drawing based on a command string.
     * The command is expected to match the pattern for adding rectangles.
     *
     * @param command the command string to add a rectangle (e.g., "add rectangle 10 10 5 20 r")
     */
    public void addRectangle(String command) {

            Matcher matcher = Pattern.compile(CommandPattern.ADD_RECTANGLE.getPattern()).matcher(command);
            if (matcher.find()) {
                double upperLeftX = Double.parseDouble(matcher.group(1));
                double upperLeftY = Double.parseDouble(matcher.group(2));
                double width = Double.parseDouble(matcher.group(3));
                double height = Double.parseDouble(matcher.group(4));
                char color = matcher.group(5).charAt(0);
                drawing.addShape(new Rectangle(new Point(upperLeftX, upperLeftY), width, height, color));
                View.display("Rectangle added.");
            } else {
                View.display("Invalid rectangle command.");
            }

    }

    /**
     * Adds a square to the drawing based on a command string.
     * The command is expected to match the pattern for adding squares.
     *
     * @param command the command string to add a square (e.g., "add square 10 10 5 r")
     */
    public void addSquare(String command) {

            Matcher matcher = Pattern.compile(CommandPattern.ADD_SQUARE.getPattern()).matcher(command);
            if (matcher.find()) {
                double upperLeftX = Double.parseDouble(matcher.group(1));
                double upperLeftY = Double.parseDouble(matcher.group(2));
                double side = Double.parseDouble(matcher.group(3));
                char color = matcher.group(4).charAt(0);
                drawing.addShape(new Square(new Point(upperLeftX, upperLeftY), side, color));
                View.display("Square added.");
            } else {
                View.display("Invalid square command.");
            }

    }

    /**
     * Moves a shape in the drawing based on a command string.
     * The command is expected to match the pattern for moving shapes.
     *
     * @param command the command string to move a shape (e.g., "move 1 10 5")
     */
    public void moveShape(String command) {
            Matcher matcher = Pattern.compile(CommandPattern.MOVE.getPattern()).matcher(command);
            if (matcher.find()) {
                int index = Integer.parseInt(matcher.group(1));
                double dx = Double.parseDouble(matcher.group(2));
                double dy = Double.parseDouble(matcher.group(3));

                Shape shape = Drawing.getShapeAt(index);

                if (shape != null) {
                    shape.move(dx, dy);
                    View.display("Shape moved.");
                }
            } else {
                View.display("Invalid move command.");
            }
    }

    /**
     * Removes a shape from the drawing based on a command string.
     * The command is expected to match the pattern for deleting shapes.
     *
     * @param command the command string to delete a shape (e.g., "delete 1")
     */
    public void removeShape(String command) {
            Matcher matcher = Pattern.compile(CommandPattern.DELETE.getPattern()).matcher(command);
            if (matcher.find()) {
                int index = Integer.parseInt(matcher.group(1));
                Shape shape = Drawing.getShapeAt(index);
                List<Shape> shapes = Drawing.getShapes();
                if (shape != null) {
                    shapes.remove(shape);
                    View.display("Shape deleted.");
                }
            } else {
                View.display("Invalid delete command.");
            }

    }

    /**
     * Changes the color of a shape in the drawing based on a command string.
     * The command is expected to match the pattern for changing colors.
     *
     * @param command the command string to change a shape's color (e.g., "color 1 B")
     */
    public void setColor(String command) {

            Matcher matcher = Pattern.compile(CommandPattern.COLOR.getPattern()).matcher(command);
            if (matcher.find()) {
                int index = Integer.parseInt(matcher.group(1));
                char color = matcher.group(2).charAt(0);

                Shape shape = Drawing.getShapeAt(index);
                if (shape != null) {
                    shape.setColor(color);
                }
            } else {
                View.display("Invalid color command.");
            }

    }

    /**
     * Gets the color of a shape at a specific position in the drawing grid.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the color of the shape at the given position, or 0 if no shape exists there
     */
    public char getColor(int x, int y) {
        Shape shape = drawing.getShapeAt(new Point(x, y));
        return (shape != null) ? shape.getColor() : 0;
    }

    /**
     * Gets the width of the drawing grid.
     *
     * @return the width of the drawing
     */
    public int getWidth() {
        return drawing.getWidth();
    }

    /**
     * Gets the height of the drawing grid.
     *
     * @return the height of the drawing
     */
    public int getHeight() {
        return drawing.getHeight();
    }
}
