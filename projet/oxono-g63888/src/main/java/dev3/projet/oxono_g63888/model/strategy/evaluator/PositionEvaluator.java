package dev3.projet.oxono_g63888.model.strategy.evaluator;

import dev3.projet.oxono_g63888.model.*;
import static dev3.projet.oxono_g63888.model.strategy.evaluator.ScoreConstants.*;

public class PositionEvaluator {
    private final ColorPawn playerColor;
    private final ThreatDetector threatDetector;

    public PositionEvaluator(ColorPawn playerColor) {
        this.playerColor = playerColor;
        this.threatDetector = new ThreatDetector(playerColor);
    }

    public int evaluatePosition(Board board, Position pos, Pawn pawn) {
        int score = 0;
        
        // Évaluation de la position stratégique
        score += evaluateStrategicPosition(board, pos, pawn);
        
        // Évaluation des menaces
        score += threatDetector.detectThreats(board, pos, pawn);
        
        // Bonus/Malus en fonction de la mobilité
        score += evaluateMobility(board, pos);

        return score;
    }

    private int evaluateStrategicPosition(Board board, Position pos, Pawn pawn) {
        int score = 0;
        int center = board.getSize() / 2;
        
        // Contrôle du centre
        int distanceToCenter = Math.abs(pos.row() - center) + Math.abs(pos.column() - center);
        score += (CENTER_CONTROL_SCORE / (distanceToCenter + 1)) * (pawn.getColor() == playerColor ? 1 : -1);
        
        // Pénalité pour les coins
        if (isCorner(pos, board.getSize())) {
            score += CORNER_PENALTY;
        }
        
        return score;
    }

    private boolean isCorner(Position pos, int size) {
        return (pos.row() == 0 || pos.row() == size - 1) && 
               (pos.column() == 0 || pos.column() == size - 1);
    }

    private int evaluateMobility(Board board, Position pos) {
        int mobility = 0;
        ;
        
        for (Direction dir : Direction.values()) {
            Position newPos = new Position(pos.row() + dir.getDeltaX(), pos.column() + dir.getDeltaY());
            if (isValidPosition(newPos, board.getSize()) && board.getToken(newPos) == null) {
                mobility++;
            }
        }
        
        return mobility * MOBILITY_SCORE;
    }

    private boolean isValidPosition(Position pos, int size) {
        return pos.row() >= 0 && pos.row() < size && 
               pos.column() >= 0 && pos.column() < size;
    }
}