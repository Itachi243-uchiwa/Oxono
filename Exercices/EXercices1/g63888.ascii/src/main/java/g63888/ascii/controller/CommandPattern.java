package g63888.ascii.controller;

public enum CommandPattern {
        ADD_CIRCLE("add circle (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\w)"),
        ADD_RECTANGLE("add rectangle (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\w)"),
        ADD_SQUARE("add square (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?) (\\w)"),
        MOVE("move (\\d+) (\\d+) (\\d+)"),
        COLOR("color (\\d+) (\\w)"),
        DELETE("delete (\\d+)"),
        LIST("list"),
        SHOW("show");

        private final String pattern;

        CommandPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }


