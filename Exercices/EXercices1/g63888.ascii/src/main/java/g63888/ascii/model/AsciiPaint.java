package g63888.ascii.model;

import g63888.ascii.Util.Command;
import g63888.ascii.Util.CommandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AsciiPaint {

    private final Drawing drawing;
    private final CommandManager command = new CommandManager();


    public AsciiPaint(int width, int height) {
        this.drawing = new Drawing(width, height);
    }


    /**
     * Adds a circle to the drawing grid at the specified center coordinates, with the given radius and color.
     *
     * @param centerX the x-coordinate of the center of the circle
     * @param centerY the y-coordinate of the center of the circle
     * @param radius the radius of the circle
     * @param color the color of the circle
     */
    public void addCircle(double centerX, double centerY, double radius, char color) {
        Shape shape = new Circle(new Point(centerX, centerY), radius, color);
        command.do_(new AddCommand(drawing, shape));
    }

    /**
     * Adds a rectangle to the drawing grid at the specified upper-left coordinates, with the given width, height, and color.
     *
     * @param upperLeftX the x-coordinate of the upper-left corner of the rectangle
     * @param upperLeftY the y-coordinate of the upper-left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color of the rectangle
     */
    public void addRectangle(double upperLeftX, double upperLeftY, double width, double height, char color) {
        Shape shape = new Rectangle(new Point(upperLeftX, upperLeftY),width,height,color);
        command.do_(new AddCommand(drawing, shape));

    }

    /**
     * Adds a square to the drawing grid at the specified upper-left coordinates, with the given side length and color.
     *
     * @param upperLeftX the x-coordinate of the upper-left corner of the square
     * @param upperLeftY the y-coordinate of the upper-left corner of the square
     * @param side the side length of the square
     * @param color the color of the square
     */
    public void addSquare(double upperLeftX, double upperLeftY, double side, char color) {
        Shape shape = new Square(new Point(upperLeftX, upperLeftY), side, color);
        command.do_(new AddCommand(drawing, shape));
    }


    /**
     * Moves a shape at the specified index in the drawing grid to the new coordinates (x, y).
     *
     * @param index the index of the shape to be moved in the drawing grid
     * @param x the new x-coordinate of the shape
     * @param y the new y-coordinate of the shape
     */
    public void moveShape(int index, double x, double y) {
        Shape shape = drawing.getShapeAt(index);
        if (shape != null) {
            shape.move(x, y);
        }
    }

    /**
     * Removes a shape at the specified index from the drawing grid.
     *
     * @param index the index of the shape to be removed in the drawing grid
     */
    public void removeShape(int index) {
        command.do_(new DeleteCommand(drawing, index));

    }

    /**
     * Changes the color of a shape at the specified index in the drawing grid.
     *
     * @param index the index of the shape to be colored in the drawing grid
     * @param color the new color of the shape
     */
    public void setColor(int index, char color) {
        command.do_(new ChangeColorCommand(drawing, index, color));

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
        return (shape != null) ? shape.getColor() : ' ';
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

    public void group (List<Integer> indexes) {

        Random random = new Random();
        Group group = new Group( (char) (random.nextInt(26) + 'A'));

        for (int index : indexes) {
            Shape shape = drawing.getShapeAt(index);
            if (shape != null ) {
                group.addShape(shape);
                drawing.removeShape(shape);
            }
        }
        drawing.addShape(group);
    }

    public void ungroup(int index) {
        Shape shape = drawing.getShapeAt(index);

        if (shape instanceof Group group) {
            for (Shape subshape : group.getShapes()) {
                drawing.addShape(subshape);
            }
            drawing.removeShape(shape);
        } else
            System.out.println("Selected shape is not a group");



    }
    public void undo() {
        command.undo();
    }
    public void redo() {
        command.redo();
    }

}
