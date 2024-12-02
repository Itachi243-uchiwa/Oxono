package dev3.projet.oxono_g63888.model.strategy.evaluation;

import dev3.projet.oxono_g63888.model.*;

/**
 * Évaluateur des aspects positionnels du jeu
 */
public class PositionalEvaluator {
    private static final int CENTER_CONTROL_SCORE = 500;
    private static final int CORNER_PENALTY = -300;
    
    private final ColorPawn aiColor;

    public PositionalEvaluator(ColorPawn aiColor) {
        this.aiColor = aiColor;
    }

    /**
     * Évalue les aspects positionnels du plateau
     */
    public int evaluate(Board board) {
        int score = 0;
        int boardSize = board.getSize();
        int center = boardSize / 2;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);

                if (token instanceof Pawn pawn) {
                    // Contrôle du centre
                    int distanceToCenter = Math.abs(row - center) + Math.abs(col - center);
                    int positionScore = CENTER_CONTROL_SCORE / (distanceToCenter + 1);

                    // Pénalité pour les coins
                    if (isCorner(row, col, boardSize)) {
                        positionScore += CORNER_PENALTY;
                    }

                    score += pawn.getColor() == aiColor ? positionScore : -positionScore;
                }
            }
        }

        return score;
    }

    private boolean isCorner(int row, int col, int boardSize) {
        return (row == 0 || row == boardSize - 1) && 
               (col == 0 || col == boardSize - 1);
    }
}