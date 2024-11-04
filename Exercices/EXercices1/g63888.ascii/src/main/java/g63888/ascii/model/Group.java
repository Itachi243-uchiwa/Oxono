package g63888.ascii.model;

import java.util.ArrayList;
import java.util.List;

public class Group extends ColoredShape {

    List<Shape> shapes;

    public Group(char color) {
        super(color);
        shapes = new ArrayList<>();
    }

    @Override
    public void move(double dx, double dy) {
        shapes.forEach(shape -> shape.move(dx, dy));
    }


    @Override
    public boolean isInside(Point point) {
        return shapes.stream().anyMatch(shape -> shape.isInside(point));
    }


    public void  addShape(Shape shape) {
        shapes.add(shape);
    }

    public List<Shape> getShapes() {
        return this.shapes;
    }
}
