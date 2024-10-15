package g63888.ascii.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DrawingTest {

    @Test
    void getShapeAt_ShouldReturnShape_WhenShapeExistsAtGivenIndex() {
        Drawing drawing = new Drawing(100, 100);
        Shape circle = new Circle(new Point(50, 50), 10, 'o');
        drawing.addShape(circle);
        Shape result = Drawing.getShapeAt(0);
        Assertions.assertEquals(circle, result);
    }

    @Test
    void getShapeAt_ShouldReturnNull_WhenShapeDoesntExistsAtGivenIndex() {
        Drawing drawing = new Drawing(100, 100);
        Shape result = Drawing.getShapeAt(0);
        Assertions.assertNull(result);
    }

    @Test
    void getShapeAt_ShouldThrowException_WhenNegativeIndexGiven() {
        Drawing drawing = new Drawing(100, 100);
        Assertions.assertNull(Drawing.getShapeAt(-1));
    }

    @Test
    void getShapeAt_ShouldReturnNull_WhenIndexOutOfBoundsGiven() {
        Drawing drawing = new Drawing(100, 100);
        Shape result = Drawing.getShapeAt(100);
        Assertions.assertNull(result);
    }
}