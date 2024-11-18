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
     * Génère le prochain mouvement en sélectionnant une position d'insertion aléatoire
     * et crée un nouveau pion à cette position.
     *
     * @param board Le plateau de jeu sur lequel le mouvement sera effectué.
     * @return Un mouvement aléatoire avec un nouveau pion à une position aléatoire.
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
     * Détermine le prochain mouvement aléatoire pour le totem en choisissant une position aléatoire
     * parmi les positions possibles pour déplacer le totem.
     *
     * @param board Le plateau de jeu sur lequel le totem sera déplacé.
     * @return Un mouvement aléatoire pour le totem vers une nouvelle position.
     */
    @Override
    public Move getNextTotemMove(Board board) {
        Mark[] marks = {Mark.X, Mark.O};
        Mark mark = marks[random.nextInt(marks.length)];

        Position oldPosTotem = board.getTotemPosition(mark);

        Totem totem = board.getTotem(oldPosTotem);
        List<Position> positionList = board.getMovesPossibles(oldPosTotem);

        int randomIndex = random.nextInt(positionList.size());
        positionTotem = positionList.get(randomIndex);

        return new Move(totem, positionTotem);
    }
}
