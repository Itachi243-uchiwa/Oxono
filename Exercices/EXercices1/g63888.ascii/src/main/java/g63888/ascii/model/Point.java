package g63888.ascii.model;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Moves the point by specified distances along the x and y axes.
     *
     * @param dx the distance to move the point along the x-axis
     * @param dy the distance to move the point along the y-axis
     */
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public double distanceTo(Point other) {
        return Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2));
    }
}
