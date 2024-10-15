package g63888.ascii;

import g63888.ascii.controller.AsciiController;
import g63888.ascii.model.AsciiPaint;
import g63888.ascii.view.View;

public class App {
    public static void main(String[] args) {

    AsciiController asciiPaint = new AsciiController(new AsciiPaint(60,60));
    asciiPaint.start();

    }
}

