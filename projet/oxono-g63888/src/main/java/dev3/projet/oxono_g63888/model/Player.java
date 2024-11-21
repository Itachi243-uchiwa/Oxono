package dev3.projet.oxono_g63888.model;

import java.util.Stack;

public class Player {

    private ColorPawn color;
    private Stack<Pawn> pawnsX;
    private Stack<Pawn> pawnsO;


    public Player(ColorPawn color) {
        this.color = color;
        this.pawnsX = new Stack<>();
        this.pawnsO = new Stack<>();
        initializePawns();
    }

    /**
     * Initializes the stacks of pawns with 8 pawns of type Mark.X and 8 pawns of type Mark.O.
     * The color of the pawns matches the player's color.
     */
    private void initializePawns() {
        for (int i = 0; i < 8; i++) {
            pawnsX.push(new Pawn(color, Mark.X));
            pawnsO.push(new Pawn(color, Mark.O));
        }
    }

    /**
     * Returns the color of the player.
     *
     * @return The color associated with the player.
     */
    public ColorPawn getColor() {
        return color;
    }

    /**
     * Checks if the player has at least one pawn of the specified type.
     *
     * @param mark The type of pawn (Mark.X or Mark.O) to check for.
     * @return true if the player has a pawn of this type, false otherwise.
     */
    public boolean hasPawn(Mark mark) {
        if (mark == Mark.X) {
            return !pawnsX.isEmpty();
        } else if (mark == Mark.O) {
            return !pawnsO.isEmpty();
        }
        return false;
    }

    /**
     * Uses a pawn of the specified type by removing the first pawn found of that type.
     *
     * @param mark The type of pawn to use.
     */
    public void usePawn(Mark mark) {
        if (mark == Mark.X && !pawnsX.isEmpty()) {
            pawnsX.pop();
        } else if (mark == Mark.O && !pawnsO.isEmpty()) {
            pawnsO.pop();
        }
    }

    /**
     * Adds a pawn of the specified type to the player's stack of pawns, simulating the return of a pawn.
     *
     * @param mark The type of pawn to add.
     */
    public void returnPawn(Mark mark) {
        if (mark == Mark.X) {
            pawnsX.push(new Pawn(color, Mark.X));
        } else if (mark == Mark.O) {
            pawnsO.push(new Pawn(color, Mark.O));
        }
    }

    /**
     * Counts the remaining pawns of a given type in the player's stack of pawns.
     *
     * @param mark The type of pawn (Mark.X or Mark.O) to count.
     * @return The number of pawns of this type.
     */
    public int getRemainingPawns(Mark mark) {
        if (mark == Mark.X) {
            return pawnsX.size();
        } else if (mark == Mark.O) {
            return pawnsO.size();
        }
        return 0;
    }

    public boolean dontHaveAnyPawns() {
        return pawnsX.isEmpty() && pawnsO.isEmpty();
    }

    /**
     * Returns a string representation of the player's state.
     *
     * @return A string containing player information.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + color;
    }
}
