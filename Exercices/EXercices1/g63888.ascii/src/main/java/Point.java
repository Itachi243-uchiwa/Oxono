class Point {
    private double x;
    private double y;

    public Point() {
        this(0.0D, 0.0D);
    }

    public Point(double var1, double var3) {
        this.x = var1;
        this.y = var3;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void move(double var1, double var3) {
        this.x += var1;
        this.y += var3;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    }

class TestPoint {
    public static void main(String[] var0) {
        Point var1 = new Point();
        System.out.println(var1);
        var1.move(2.0D, 2.0D);
        System.out.println(var1);
    }
}




