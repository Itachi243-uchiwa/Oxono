package dev3.projet.oxono_g63888.model.strategy.evaluation;

import dev3.projet.oxono_g63888.model.*;

/**
 * Évaluateur des menaces et opportunités
 */
public class ThreatEvaluator {
    private static final int WIN_SCORE = 1000000;
    private static final int DANGEROUS_ALIGNMENT_SCORE = 250000;
    private static final int POTENTIAL_WIN_SCORE = 300000;
    private static final int TWO_COLOR_ALIGNMENT_RISK = 700000;

    private final ColorPawn aiColor;

    public ThreatEvaluator(ColorPawn aiColor) {
        this.aiColor = aiColor;
    }

    /**
     * Évalue les menaces sur le plateau
     */
    public int evaluate(Board board) {
        int score = 0;
        ColorPawn opponentColor = (aiColor == ColorPawn.BLACK) ? ColorPawn.PINK : ColorPawn.BLACK;

        // Menaces immédiates
        score += evaluateImmediateThreats(board, aiColor);
        score -= evaluateImmediateThreats(board, opponentColor);

        // Menaces potentielles
        score += evaluatePotentialThreats(board, aiColor);
        score -= evaluatePotentialThreats(board, opponentColor);

        return score;
    }

    private int evaluateImmediateThreats(Board board, ColorPawn color) {
        int score = 0;
        int size = board.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);

                if (token instanceof Pawn pawn && pawn.getColor() == color) {
                    // Vérifie les alignements dangereux
                    if (hasThreeAligned(board, pos, color)) {
                        score += DANGEROUS_ALIGNMENT_SCORE;
                    }

                    // Vérifie les possibilités de victoire
                    if (checkTwoColorAlignmentRisk(board, pos, pawn) >= 2) {
                        score += POTENTIAL_WIN_SCORE;
                    }
                }
            }
        }

        return score;
    }

    private int evaluatePotentialThreats(Board board, ColorPawn color) {
        int score = 0;
        int size = board.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position pos = new Position(row, col);
                if (board.getToken(pos) == null) {
                    score += evaluateEmptyPosition(board, pos, color);
                }
            }
        }

        return score;
    }

    private boolean hasThreeAligned(Board board, Position pos, ColorPawn color) {
        int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}};
        
        for (int[] dir : directions) {
            if (countAligned(board, pos, dir[0], dir[1], color) >= 3) {
                return true;
            }
        }
        
        return false;
    }

    private int countAligned(Board board, Position pos, int dx, int dy, ColorPawn color) {
        int count = 1;
        int size = board.getSize();

        // Vérifie dans les deux directions
        for (int factor : new int[]{1, -1}) {
            int r = pos.row() + dx * factor;
            int c = pos.column() + dy * factor;
            
            while (r >= 0 && r < size && c >= 0 && c < size) {
                Token token = board.getToken(new Position(r, c));
                if (token instanceof Pawn pawn && pawn.getColor() == color) {
                    count++;
                } else {
                    break;
                }
                r += dx * factor;
                c += dy * factor;
            }
        }

        return count;
    }

    private int checkTwoColorAlignmentRisk(Board board, Position pos, Pawn pawn) {
        int risk = 0;
        int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}};

        for (int[] dir : directions) {
            int aligned = countAligned(board, pos, dir[0], dir[1], pawn.getColor());
            if (aligned >= 2 && hasEmptyAdjacent(board, pos, dir[0], dir[1])) {
                risk++;
            }
        }

        return risk;
    }

    private boolean hasEmptyAdjacent(Board board, Position pos, int dx, int dy) {
        int size = board.getSize();
        
        // Vérifie les deux extrémités
        for (int factor : new int[]{1, -1}) {
            int r = pos.row() + dx * factor;
            int c = pos.column() + dy * factor;
            
            if (r >= 0 && r < size && c >= 0 && c < size) {
                if (board.getToken(new Position(r, c)) == null) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private int evaluateEmptyPosition(Board board, Position pos, ColorPawn color) {
        int score = 0;
        int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}};

        for (int[] dir : directions) {
            int consecutive = countConsecutiveInDirection(board, pos, dir[0], dir[1], color);
            if (consecutive >= 2) {
                score += POTENTIAL_WIN_SCORE / 2;
            }
        }

        return score;
    }

    private int countConsecutiveInDirection(Board board, Position pos, int dx, int dy, ColorPawn color) {
        int count = 0;
        int size = board.getSize();

        for (int factor : new int[]{1, -1}) {
            int r = pos.row() + dx * factor;
            int c = pos.column() + dy * factor;
            
            while (r >= 0 && r < size && c >= 0 && c < size) {
                Token token = board.getToken(new Position(r, c));
                if (token instanceof Pawn pawn && pawn.getColor() == color) {
                    count++;
                } else {
                    break;
                }
                r += dx * factor;
                c += dy * factor;
            }
        }

        return count;
    }
}