package dev3.projet.oxono_g63888.model.strategy;


import dev3.projet.oxono_g63888.model.AIPlayer;
import dev3.projet.oxono_g63888.model.Board;

public interface AIStrategy {
    Move getNextMove(Board board);

    Move getNextTotemMove(Board board, AIPlayer ai);
}
