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


    @Override
    public Move getNextMove(Board board) {
        List<Position> possibleMoves = board.getInsertionPositions(positionTotem);

        int randomIndex = random.nextInt(possibleMoves.size());
        Position randomPosition = possibleMoves.get(randomIndex);
        Pawn randomPawn = new Pawn(color, board.getTotem(positionTotem).getMark());

        return new Move(randomPawn, randomPosition);
    }

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
