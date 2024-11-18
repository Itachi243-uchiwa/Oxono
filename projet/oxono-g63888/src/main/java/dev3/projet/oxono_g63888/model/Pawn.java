package dev3.projet.oxono_g63888.model;

public class Pawn extends Token {

    public static final String ANSI_PINK = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";

    private ColorPawn color;

    public Pawn(ColorPawn color, Mark mark) {
        super(mark);
        this.color = color;
    }

    public ColorPawn getColor() {
        return color;
    }

    @Override
    public String toString() {

        return color == ColorPawn.PINK ? ANSI_PINK + getMark() + ANSI_RESET : String.valueOf(getMark());
    }
}
