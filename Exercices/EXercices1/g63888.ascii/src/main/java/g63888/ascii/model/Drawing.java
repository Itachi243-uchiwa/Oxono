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

    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public Shape getShapeAt(int index) {
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
