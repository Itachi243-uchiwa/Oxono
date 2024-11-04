package g63888.ascii.model;

import g63888.ascii.Util.Command;

public class ChangeColorCommand implements Command {

    private Drawing drawing;
    private int index;
    private char newColor;
    private char oldColor;

    public ChangeColorCommand(Drawing drawing, int index, char newColor) {

        this.drawing = drawing;
        this.index = index;
        this.newColor = newColor;

    }

    /**
     * Executes the command by changing the color of the shape at the specified index in the drawing.
     */
    @Override
    public void execute() {
        Shape shape = drawing.getShapeAt(index);
        if (shape != null) {
            oldColor = shape.getColor();
            shape.setColor(newColor);
        }
    }


    /**
     * Reverts the color change made by the {@link ChangeColorCommand} by setting the color of the shape at the specified index in the drawing back to its original color.
     */
    @Override
    public void unexecute() {
        Shape shape = drawing.getShapeAt(index);
        shape.setColor(oldColor);
    }
}
