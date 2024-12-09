package dev3.projet.oxono_g63888.model.strategy.evaluator;

import dev3.projet.oxono_g63888.model.*;
import static dev3.projet.oxono_g63888.model.strategy.evaluator.ScoreConstants.*;

public class PatternAnalyzer {
    private final ColorPawn playerColor;

    public PatternAnalyzer(ColorPawn playerColor) {
        this.playerColor = playerColor;
    }

    public int analyzeForkThreats(Board board, Position pos, Pawn pawn) {
        int threatCount = 0;
        for (Direction dir1 : Direction.values()) {
            for (Direction dir2 : Direction.values()) {
                if (dir1 == dir2) continue;
                if (isDoubleThreat(board, pos, pawn, dir1, dir2)) {
                    threatCount++;
                }
            }
        }
        return threatCount * FORK_THREAT_SCORE;
    }

    private boolean isDoubleThreat(Board board, Position pos, Pawn pawn, Direction dir1, Direction dir2) {
        int count1 = LineCounter.countConsecutiveSameColor(board, pos, dir1.getDeltaX(), dir1.getDeltaY(), pawn.getColor());
        int count2 = LineCounter.countConsecutiveSameColor(board, pos, dir2.getDeltaX(), dir2.getDeltaY(), pawn.getColor());
        return count1 >= 2 && count2 >= 2;
    }

    public int evaluateDefensiveFormation(Board board, Position pos, Pawn pawn) {
        int score = 0;
        if (isDefensivePosition(board, pos)) {
            score += DEFENSIVE_BONUS;
            if (pawn.getColor() == playerColor) {
                score += PATTERN_BONUS;
            }
        }
        return score;
    }

    private boolean isDefensivePosition(Board board, Position pos) {
        int adjacentAllies = countAdjacentAllies(board, pos);
        return adjacentAllies >= 2;
    }

    private int countAdjacentAllies(Board board, Position pos) {
        int count = 0;
        
        for (Direction dir : Direction.values()) {
            Position adjacent = new Position(pos.row() + dir.getDeltaX(), pos.column() + dir.getDeltaY());
            if (isValidPosition(adjacent, board.getSize())) {
                Pawn pawn = BoardUtils.isPawn(adjacent, board);
                if (pawn != null && pawn.getColor() == playerColor) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isValidPosition(Position pos, int size) {
        return pos.row() >= 0 && pos.row() < size && pos.column() >= 0 && pos.column() < size;
    }
}