package g63888.ascii.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AsciiPaintTest {

    @Test
    void getColor_shapeAtPoint_returnsCorrectColor() {
        AsciiPaint asciiPaint = new AsciiPaint(10, 10);
        asciiPaint.addSquare(1, 1, 5, 'a');
        assertEquals('a', asciiPaint.getColor(3, 3));
    }

    @Test
    void getColor_noShapeAtPoint_returnsZero() {
        AsciiPaint asciiPaint = new AsciiPaint(10, 10);
        asciiPaint.addSquare(1, 1, 5, 'a');
        assertEquals(0, asciiPaint.getColor(6, 6));
    }

    @Test
    void getColor_shapeAtPointAfterMoveShape_returnsCorrectColor() {
        AsciiPaint asciiPaint = new AsciiPaint(10, 10);
        asciiPaint.addSquare(1, 1, 5, 'a');
        asciiPaint.moveShape(0, 2, 2);
        assertEquals('a', asciiPaint.getColor(5, 5));
    }

    @Test
    void getColor_shapeAtPointAfterRemoveShape_returnsZero() {
        AsciiPaint asciiPaint = new AsciiPaint(10, 10);
        asciiPaint.addSquare(1, 1, 5, 'a');
        asciiPaint.removeShape(0);
        assertEquals(0, asciiPaint.getColor(3, 3));
    }

    @Test
    void getColor_shapeAtPointAfterSetColor_returnsNewColor() {
        AsciiPaint asciiPaint = new AsciiPaint(10, 10);
        asciiPaint.addSquare(1, 1, 5, 'a');
        asciiPaint.setColor(0, 'b');
        assertEquals('b', asciiPaint.getColor(3, 3));
    }

}
