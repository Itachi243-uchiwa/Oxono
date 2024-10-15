package g63888.ascii.model;

public class Rectangle extends ColoredShape {

    private final Point upperLeft;
    private final double width;
    private final double height;


    public Rectangle(Point upperLeft, double width, double height, char color) {
        super(color);
        this.upperLeft = upperLeft;
        this.width = width;
        this.height = height;
    }

    /**
     * Moves the upper-left corner of the rectangle by specified distances along the x and y axes.
     *
     * @param dx the distance to move the upper-left corner along the x-axis
     * @param dy the distance to move the upper-left corner along the y-axis
     */
    @Override
    public void move(double dx, double dy) {
        upperLeft.move(dx, dy);
    }

    /**
     * Checks if the specified point is inside the rectangle.
     *
     * @param point the point to be checked
     * @return true if the point is inside the rectangle, false otherwise
     */
    @Override
    public boolean isInside(Point point) {
        return point.getX() >= upperLeft.getX() && point.getX() <= upperLeft.getX() + width
                && point.getY() >= upperLeft.getY() && point.getY() <= upperLeft.getY() + height;
    }
}
