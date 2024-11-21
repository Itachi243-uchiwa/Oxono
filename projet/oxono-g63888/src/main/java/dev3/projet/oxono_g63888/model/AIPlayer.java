package dev3.projet.oxono_g63888.model;


import dev3.projet.oxono_g63888.model.strategy.AIStrategy;
import dev3.projet.oxono_g63888.model.strategy.Move;

public class AIPlayer extends Player {
    private final AIStrategy strategy;

    public AIPlayer(ColorPawn color, AIStrategy strategy) {
        super(color);
        this.strategy = strategy;
    }

    public Move getNextMove(Board board) {
        return strategy.getNextMove(board);
    }

    public Move getNextTotemMove(Board board) {
        return strategy.getNextTotemMove(board, this);
    }
}