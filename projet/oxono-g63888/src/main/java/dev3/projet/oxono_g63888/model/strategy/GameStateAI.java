package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.Board;
import dev3.projet.oxono_g63888.model.ColorPawn;

public class GameStateAI {
    private final Board board;
    private final ColorPawn currentPlayer;

    public GameStateAI(Board board, ColorPawn currentPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
    }

    public Board getBoard() { return board; }
    public ColorPawn getCurrentPlayer() { return currentPlayer; }
}