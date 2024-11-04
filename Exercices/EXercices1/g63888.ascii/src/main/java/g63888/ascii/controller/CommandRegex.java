package g63888.ascii.controller;

import g63888.ascii.model.*;
import g63888.ascii.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CommandRegex {

    ADD_CIRCLE("add circle (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\w)") {
        @Override
        public void execute(AsciiPaint paint, String command) {
            Matcher matcher = Pattern.compile(this.getPattern()).matcher(command);
            if (matcher.find()) {
                double centerX = Double.parseDouble(matcher.group(1));
                double centerY = Double.parseDouble(matcher.group(2));
                double radius = Double.parseDouble(matcher.group(3));
                char color = matcher.group(4).charAt(0);
                paint.addCircle(centerX, centerY, radius, color);
            } else {
                View.display("Invalid circle command.");
            }
        }
    },

    ADD_RECTANGLE("add rectangle (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\w)") {
        @Override
        public void execute(AsciiPaint paint, String command){
            Matcher matcher = Pattern.compile(ADD_RECTANGLE.getPattern()).matcher(command);
            if (matcher.find()) {
                double upperLeftX = Double.parseDouble(matcher.group(1));
                double upperLeftY = Double.parseDouble(matcher.group(2));
                double width = Double.parseDouble(matcher.group(3));
                double height = Double.parseDouble(matcher.group(4));
                char color = matcher.group(5).charAt(0);
                paint.addRectangle(upperLeftX, upperLeftY, width, height, color);
                View.display("Rectangle added.");
            } else {
                View.display("Invalid rectangle command.");
            }
        }
    },
    ADD_SQUARE("add square (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\w)"){
        @Override
        public void execute(AsciiPaint paint, String command) {
            Matcher matcher = Pattern.compile(ADD_SQUARE.getPattern()).matcher(command);
            if (matcher.find()) {
                double upperLeftX = Double.parseDouble(matcher.group(1));
                double upperLeftY = Double.parseDouble(matcher.group(2));
                double side = Double.parseDouble(matcher.group(3));
                char color = matcher.group(4).charAt(0);
                paint.addSquare(upperLeftX, upperLeftY, side, color);
                View.display("Square added.");
            } else {
                View.display("Invalid square command.");
            }
        }
    },
    MOVE("move (\\d+) (\\d+) (\\d+)"){
        @Override
        public void execute(AsciiPaint paint, String command) {
            Matcher matcher = Pattern.compile(MOVE.getPattern()).matcher(command);
            if (matcher.find()) {
                int index = Integer.parseInt(matcher.group(1));
                double dx = Double.parseDouble(matcher.group(2));
                double dy = Double.parseDouble(matcher.group(3));

                paint.moveShape(index, dx, dy);
            } else {
                View.display("Invalid move command.");
            }
        }
    },
    COLOR("color (\\d+) (\\w)") {
        @Override
        public void execute(AsciiPaint paint, String command) {
            Matcher matcher = Pattern.compile(COLOR.getPattern()).matcher(command);
            if (matcher.find()) {
                int index = Integer.parseInt(matcher.group(1));
                char color = matcher.group(2).charAt(0);
                paint.setColor(index, color);
            } else {
                View.display("Invalid color command.");
            }
        }
    },

    DELETE("delete (\\d+)") {
        @Override
        public void execute(AsciiPaint paint, String command) {
            Matcher matcher = Pattern.compile(DELETE.getPattern()).matcher(command);
            if (matcher.find()) {
                int index = Integer.parseInt(matcher.group(1));
                paint.removeShape(index);
            } else {
                View.display("Invalid delete command.");
            }
        }
    },
    GROUP("\\bgroup\\b(?:\\s+(\\d+))+"){
        @Override
        public void execute(AsciiPaint paint, String command) {
            Matcher matcher = Pattern.compile(GROUP.getPattern()).matcher(command);
            List<Integer> numbers = new ArrayList<>();
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String numberStr = matcher.group(i);
                    if (numberStr != null) {
                        numbers.add(Integer.parseInt(numberStr));
                    }
                }
            } paint.group(numbers);

        }
    },
    UNGROUP("ungroup (\\d+)"){
        @Override
        public void execute(AsciiPaint paint, String command) {
            Matcher matcher = Pattern.compile(UNGROUP.getPattern()).matcher(command);
            if (matcher.find()) {
                int index = Integer.parseInt(matcher.group(1));
                paint.ungroup(index);

            }
        }
    },
    UNDO("undo") {
        @Override
        public void execute(AsciiPaint paint, String command) {
            paint.undo();
        }
    },
    REDO("redo") {
        @Override
        public void execute(AsciiPaint paint, String command) {
            paint.redo();
        }
    },
    SHOW("show"){
        @Override
        public void execute(AsciiPaint paint, String command){
           View view = new View(paint);
           view.showShapes();
        }
    },
    LIST("list"){
        @Override
        public void execute(AsciiPaint paint, String command) {
            View view = new View(paint);
            view.listShapes();
        }
    };

    private final String pattern;

    CommandRegex(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
    public abstract void execute(AsciiPaint paint, String command);

}


