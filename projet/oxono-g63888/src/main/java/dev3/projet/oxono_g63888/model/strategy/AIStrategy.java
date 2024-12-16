package dev3.projet.oxono_g63888.model.strategy;


import dev3.projet.oxono_g63888.model.AIPlayer;
import dev3.projet.oxono_g63888.model.Board;

/**
 * The AIStrategy interface defines the contract for implementing AI strategies
 * in a board game. It provides methods for calculating the next moves
 * for a game AI, including pawn and totem moves, based on the current state of the game.
 */
public interface AIStrategy {
    Move getNextMove(Board board);

    Move getNextTotemMove(Board board, AIPlayer ai);
}
