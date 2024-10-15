package g63888.ascii.model;

import java.util.ArrayList;
import java.util.List;

public class Drawing {

    private final int width;
    private final int height;
    private static List<Shape> shapes;

    public Drawing(int width, int height) {
        this.width = width;
        this.height = height;
        shapes = new ArrayList<>();
    }
    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public boolean remove(Shape shape) {
        return shapes.remove(shape);
    }
    public  boolean remove(int index) {
        return shapes.remove(index) != null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public static Shape getShapeAt(int index) {
        if (index >= 0 && index < shapes.size()) {
            return shapes.get(index);
        }
        return null;
    }
    public Shape getShapeAt(Point point) {
        for (Shape shape : shapes) {
            if (shape.isInside(point)) {
                return shape;
            }
        }
        return null;
    }

    public static List<Shape> getShapes() {
        return shapes;
    }
}
