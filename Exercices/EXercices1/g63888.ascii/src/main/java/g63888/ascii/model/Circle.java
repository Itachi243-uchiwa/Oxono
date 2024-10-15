package g63888.ascii.model;


public class Circle extends ColoredShape {

    private Point center;
    private double radius;


    public Circle(Point center, double radius, char color) {
        super(color);
        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be positive" +
                    ", received: " + radius);
        }
        this.center = center;
        this.radius = radius;
    }

    /**
     * Moves the circle by the specified distances along the x and y axes.
     *
     * @param dx the distance to move the circle along the x-axis
     * @param dy the distance to move the circle along the y-axis
     */
    @Override
    public void move(double dx, double dy) {
        center.move(dx, dy);
    }

    /**
     * Determines whether a given point is inside the circle.
     *
     * @param point the point to be checked.
     * @return true if the point is within the circle's radius; false otherwise.
     */
    @Override
    public boolean isInside(Point point) {
        return center.distanceTo(point) <= radius;
    }
}

