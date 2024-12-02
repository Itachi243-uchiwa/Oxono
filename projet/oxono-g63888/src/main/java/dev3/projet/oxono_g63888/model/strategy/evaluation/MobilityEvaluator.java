package dev3.projet.oxono_g63888.model.strategy.evaluation;

import dev3.projet.oxono_g63888.model.*;
import java.util.*;

/**
 * Évaluateur de la mobilité des pièces
 */
public class MobilityEvaluator {
    private static final int MOBILITY_SCORE = 200;
    
    private final ColorPawn aiColor;

    public MobilityEvaluator(ColorPawn aiColor) {
        this.aiColor = aiColor;
    }

    /**
     * Évalue la mobilité des pièces sur le plateau
     */
    public int evaluate(Board board) {
        int myMoves = countAvailableMoves(board, aiColor);
        int opponentMoves = countAvailableMoves(board, getOpponentColor());
        
        return (myMoves - opponentMoves) * MOBILITY_SCORE;
    }

    private int countAvailableMoves(Board board, ColorPawn color) {
        int moves = 0;

        for (Mark mark : Mark.values()) {
            Position pos = board.getTotemPosition(mark);
            if (pos != null) {
                moves += board.getMovesPossibles(pos).size();
            }
        }

        return moves;
    }

    private ColorPawn getOpponentColor() {
        return (aiColor == ColorPawn.BLACK) ? ColorPawn.PINK : ColorPawn.BLACK;
    }
}