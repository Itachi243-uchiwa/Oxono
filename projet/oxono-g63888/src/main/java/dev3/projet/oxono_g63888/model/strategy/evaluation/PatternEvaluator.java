package dev3.projet.oxono_g63888.model.strategy.evaluation;

import dev3.projet.oxono_g63888.model.*;

/**
 * Évaluateur des motifs sur le plateau
 */
public class PatternEvaluator {
    private static final int PATTERN_SCORE = 150;
    
    private final ColorPawn aiColor;

    public PatternEvaluator(ColorPawn aiColor) {
        this.aiColor = aiColor;
    }

    /**
     * Évalue les motifs sur le plateau
     */
    public int evaluate(Board board) {
        return evaluateOrthogonalPatterns(board);
    }


    private int evaluateOrthogonalPatterns(Board board) {
        int score = 0;
        int size = board.getSize();

        // Motifs horizontaux
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size - 2; col++) {
                score += evaluatePattern(board,
                    new Position(row, col),
                    new Position(row, col + 1),
                    new Position(row, col + 2));
            }
        }

        // Motifs verticaux
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size - 2; row++) {
                score += evaluatePattern(board,
                    new Position(row, col),
                    new Position(row + 1, col),
                    new Position(row + 2, col));
            }
        }

        return score;
    }

    private int evaluatePattern(Board board, Position p1, Position p2, Position p3) {
        Token t1 = board.getToken(p1);
        Token t2 = board.getToken(p2);
        Token t3 = board.getToken(p3);

        if (!(t1 instanceof Pawn) || !(t2 instanceof Pawn) || !(t3 instanceof Pawn)) {
            return 0;
        }

        Pawn pawn1 = (Pawn) t1;
        Pawn pawn2 = (Pawn) t2;
        Pawn pawn3 = (Pawn) t3;

        int score = 0;

        // Évalue les alignements de couleur
        if (pawn1.getColor() == pawn2.getColor() && pawn2.getColor() == pawn3.getColor()) {
            score += pawn1.getColor() == aiColor ? PATTERN_SCORE : -PATTERN_SCORE;
        }

        // Évalue les alignements de marques
        if (pawn1.getMark() == pawn2.getMark() && pawn2.getMark() == pawn3.getMark()) {
            score += PATTERN_SCORE / 2;
        }

        return score;
    }
}