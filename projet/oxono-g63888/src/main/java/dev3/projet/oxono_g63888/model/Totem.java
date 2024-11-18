package dev3.projet.oxono_g63888.model;

import java.util.ArrayList;
import java.util.List;

public class Totem extends Token {


    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Totem(Mark mark) {
        super(mark);

    }

    @Override
    public String toString() {
        return ANSI_CYAN + getMark() + ANSI_RESET;
    }
}
