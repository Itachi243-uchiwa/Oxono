package dev3.projet.oxono_g63888.model;

public enum ColorPawn {

        PINK("#FFC0CB"),
        BLACK("#000000");

        private final String code;

        ColorPawn(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return name();
        }
    }

