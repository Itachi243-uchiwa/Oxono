package dev3.projet.oxono_g63888.model.strategy.evaluator;

import dev3.projet.oxono_g63888.model.*;
import static dev3.projet.oxono_g63888.model.strategy.evaluator.ScoreConstants.*;

public class ThreatDetector {
    private final ColorPawn playerColor;
    private final PatternAnalyzer patternAnalyzer;

    public ThreatDetector(ColorPawn playerColor) {
        this.playerColor = playerColor;
        this.patternAnalyzer = new PatternAnalyzer(playerColor);
    }

    public int detectThreats(Board board, Position pos, Pawn pawn) {
        int threatScore = 0;
        
        // Détection des menaces immédiates
        threatScore += detectImmediateThreats(board, pos, pawn);
        
        // Détection des menaces potentielles
        threatScore += detectPotentialThreats(board, pos, pawn);
        
        // Analyse des formations défensives
        threatScore += patternAnalyzer.evaluateDefensiveFormation(board, pos, pawn);
        
        // Détection des menaces de fourche
        threatScore += patternAnalyzer.analyzeForkThreats(board, pos, pawn);

        return threatScore;
    }

    private int detectImmediateThreats(Board board, Position pos, Pawn pawn) {
        int score = 0;
        
        // Vérification des alignements dangereux
        for (Direction dir : Direction.values()) {
            int consecutive = LineCounter.countConsecutiveSameColor(board, pos, dir.getDeltaX(), dir.getDeltaY(), pawn.getColor());
            if (consecutive >= 3) {
                score += pawn.getColor() == playerColor ? DANGEROUS_ALIGNMENT_SCORE : -DANGEROUS_ALIGNMENT_SCORE;
            }
        }
        
        // Vérification des blocages potentiels
        if (canBlockWinningMove(board, pos, pawn)) {
            score += BLOCK_WIN_SCORE;
        }

        return score;
    }

    private int detectPotentialThreats(Board board, Position pos, Pawn pawn) {
        int score = 0;
        
        // Analyse des espaces vides stratégiques
        for (Direction dir : Direction.values()) {
            int emptySpaces = LineCounter.countEmptySpacesForPotentialWin(board, pos, dir.getDeltaX(), dir.getDeltaY(), pawn.getColor());
            if (emptySpaces > 0) {
                score += pawn.getColor() == playerColor ? POTENTIAL_ALIGNMENT_SCORE : -POTENTIAL_ALIGNMENT_SCORE;
            }
        }

        return score;
    }

    private boolean canBlockWinningMove(Board board, Position pos, Pawn pawn) {
        ColorPawn opponent = (playerColor == ColorPawn.BLACK) ? ColorPawn.PINK : ColorPawn.BLACK;
        
        for (Direction dir : Direction.values()) {
            int consecutive = LineCounter.countConsecutiveSameColor(board, pos, dir.getDeltaX(), dir.getDeltaY(), opponent);
            if (consecutive >= 2) {
                int emptySpaces = LineCounter.countEmptySpacesForPotentialWin(board, pos, dir.getDeltaX(), dir.getDeltaY(), opponent);
                if (emptySpaces > 0) return true;
            }
        }
        
        return false;
    }
}