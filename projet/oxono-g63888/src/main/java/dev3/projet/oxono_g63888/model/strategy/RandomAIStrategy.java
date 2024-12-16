package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.*;

import java.util.List;
import java.util.Random;

public class RandomAIStrategy implements AIStrategy {

    private final Random random = new Random();
    private Position positionTotem = new Position(0, 0);
    private final ColorPawn color;

    public RandomAIStrategy(ColorPawn color) {
        this.color = color;
    }


    /**
     * Determines the next move of the AI player based on a list of possible positions
     * and returns the move to be played. A new pawn is created and placed at a randomly
     * chosen valid position.
     *
     * @param board the current state of the game board used to evaluate valid positions for the move
     * @return the next move, consisting of a pawn token and the target position
     */
    @Override
    public Move getNextMove(Board board) {
        List<Position> possibleMoves = board.getInsertionPositions(positionTotem);

        int randomIndex = random.nextInt(possibleMoves.size());
        Position randomPosition = possibleMoves.get(randomIndex);
        Pawn randomPawn = new Pawn(color, board.getTotem(positionTotem).getMark());

        return new Move(randomPawn, randomPosition);
    }

    /**
     * Determines the next move for the totem in a board game scenario
     * using AI strategy. The move is selected randomly from the possible
     * positions that the totem can be moved to.
     *
     * @param board the current state of the game board
     * @param aiPlayer the AI player making the move, which may possess pawns of varying marks
     * @return a Move object representing the totem being moved to a randomly selected valid position
     * @throws OxonoException if the AI player does not possess any eligible pawns for making a move
     */
    @Override
    public Move getNextTotemMove(Board board, AIPlayer aiPlayer) {
        Mark mark = null;
        if (aiPlayer.hasPawn(Mark.X) && aiPlayer.hasPawn(Mark.O)) {

            Mark[] marks = {Mark.X, Mark.O};
            mark = marks[random.nextInt(marks.length)];
        } else if (aiPlayer.hasPawn(Mark.X)) {
            mark = Mark.X;
        } else if (aiPlayer.hasPawn(Mark.O)) {
            mark = Mark.O;
        } else {
            throw new OxonoException("Ths AI don't have any Pawn");
        }


        Position oldPosTotem = board.getTotemPosition(mark);

        Totem totem = board.getTotem(oldPosTotem);
        List<Position> positionList = board.getMovesPossibles(oldPosTotem);

        int randomIndex = random.nextInt(positionList.size());
        positionTotem = positionList.get(randomIndex);

        return new Move(totem, positionTotem);
    }
}
