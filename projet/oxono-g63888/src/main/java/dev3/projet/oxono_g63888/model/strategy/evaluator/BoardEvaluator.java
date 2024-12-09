package dev3.projet.oxono_g63888.model.strategy.evaluator;

import dev3.projet.oxono_g63888.model.*;

import java.util.List;

import static dev3.projet.oxono_g63888.model.strategy.evaluator.ScoreConstants.*;

public class BoardEvaluator {
    private final ColorPawn playerColor;
    private final PositionEvaluator positionEvaluator;
    private final ThreatDetector threatDetector;
    private final PatternAnalyzer patternAnalyzer;

    public BoardEvaluator(ColorPawn playerColor) {
        this.playerColor = playerColor;
        this.positionEvaluator = new PositionEvaluator(playerColor);
        this.threatDetector = new ThreatDetector(playerColor);
        this.patternAnalyzer = new PatternAnalyzer(playerColor);
    }

    public int evaluate(Board board) {
        int score = 0;
        ColorPawn opponentColor = playerColor == ColorPawn.BLACK ? ColorPawn.PINK : ColorPawn.BLACK;

        // Évaluation des positions et menaces pour chaque pion
        score += evaluateAllPositions(board);
        
        // Évaluation des menaces globales
        score += evaluateGlobalThreats(board, playerColor, opponentColor);
        
        // Évaluation de la mobilité globale
        score += evaluateGlobalMobility(board);
        
        // Évaluation des patterns stratégiques
        score += evaluateGlobalPatterns(board);

        return score;
    }

    private int evaluateAllPositions(Board board) {
        int score = 0;
        int boardSize = board.getSize();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                Pawn pawn = BoardUtils.isPawn(pos, board);
                
                if (pawn != null) {
                    score += positionEvaluator.evaluatePosition(board, pos, pawn);
                }
            }
        }
        return score;
    }

    private int evaluateGlobalThreats(Board board, ColorPawn playerColor, ColorPawn opponentColor) {
        int score = 0;
        
        // Évaluation des menaces immédiates pour les deux joueurs
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position pos = new Position(row, col);
                Pawn pawn = BoardUtils.isPawn(pos, board);
                
                if (pawn != null) {
                    if (pawn.getColor() == playerColor) {
                        score += threatDetector.detectThreats(board, pos, pawn);
                    } else {
                        score -= threatDetector.detectThreats(board, pos, pawn);
                    }
                }
            }
        }
        
        return score;
    }

    private int evaluateGlobalMobility(Board board) {
        int playerMobility = 0;
        int opponentMobility = 0;

        for (Mark mark : Mark.values()) {
            Position totemPos = board.getTotemPosition(mark);
            if (totemPos != null) {
                List<Position> possibleMoves = board.getMovesPossibles(totemPos);
                Token token = board.getToken(totemPos);
                
                if (token instanceof Pawn pawn) {
                    if (pawn.getColor() == playerColor) {
                        playerMobility += possibleMoves.size();
                    } else {
                        opponentMobility += possibleMoves.size();
                    }
                }
            }
        }

        return (playerMobility - opponentMobility) * MOBILITY_SCORE;
    }

    private int evaluateGlobalPatterns(Board board) {
        int score = 0;
        int boardSize = board.getSize();

        // Évaluation des formations défensives
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                Pawn pawn = BoardUtils.isPawn(pos, board);
                
                if (pawn != null) {
                    if (pawn.getColor() == playerColor) {
                        score += patternAnalyzer.evaluateDefensiveFormation(board, pos, pawn);
                        score += evaluateMarkAlignment(board, pos, pawn.getMark());
                    } else {
                        score -= patternAnalyzer.evaluateDefensiveFormation(board, pos, pawn);
                        score -= evaluateMarkAlignment(board, pos, pawn.getMark());
                    }
                }
            }
        }

        return score;
    }

    private int evaluateMarkAlignment(Board board, Position pos, Mark mark) {
        int alignedCount = LineCounter.countAlignedMarks(board, pos, mark);
        return alignedCount * DANGEROUS_ALIGNMENT_SCORE;
    }

    public int getWinScore() {
        return WIN_SCORE;
    }

    public int getBlockWinScore() {
        return BLOCK_WIN_SCORE;
    }

    public int getTwoColorAlignmentRisk() {
        return TWO_COLOR_ALIGNMENT_RISK;
    }

    public int getDangerousAlignmentScore() {
        return DANGEROUS_ALIGNMENT_SCORE;
    }
}