package dev3.projet.oxono_g63888.model;

public class Pawn extends Token {

    private ColorPawn color;

    /**
     * Constructs a Pawn with a specified color and mark.
     *
     * @param color The color of the pawn, represented as a ColorPawn enum.
     * @param mark  The mark of the pawn, represented as a Mark enum (X or O).
     */
    public Pawn(ColorPawn color, Mark mark) {
        super(mark);
        this.color = color;
    }

    /**
     * Retrieves the color of the pawn.
     *
     * @return the color of the pawn as a ColorPawn enum constant
     */
    public ColorPawn getColor() {
        return color;
    }

    /**
     * Creates and returns a copy of this Pawn token.
     * The copy maintains the same color and mark as the original Pawn.
     *
     * @return a new Pawn instance with the same color and mark as this Pawn.
     */
    @Override
    public Token copy() {
        return new Pawn(this.color, this.getMark());
    }

}
