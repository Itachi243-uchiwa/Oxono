package dev3.projet.oxono_g63888.model.strategy.evaluator;

import dev3.projet.oxono_g63888.model.*;
import static dev3.projet.oxono_g63888.model.strategy.evaluator.ScoreConstants.*;
import java.util.*;

public class ThreatAnalyzer {
    private final ColorPawn playerColor;
    private final PatternAnalyzer patternAnalyzer;
    private BoardEvaluator evaluator;

    public ThreatAnalyzer(ColorPawn playerColor, BoardEvaluator evaluator) {
        this.playerColor = playerColor;
        this.patternAnalyzer = new PatternAnalyzer(playerColor);
        this.evaluator = evaluator;
    }

    public int analyzeThreats(Board board) {
        ColorPawn opponentColor = playerColor == ColorPawn.BLACK ? ColorPawn.PINK : ColorPawn.BLACK;
        return evaluateImmediateThreats(board, playerColor) -
                evaluateImmediateThreats(board, opponentColor) +
                evaluatePotentialThreats(board, playerColor) -
                evaluatePotentialThreats(board, opponentColor);
    }

    private int evaluateImmediateThreats(Board board, ColorPawn color) {
        int score = 0;
        List<ThreatInfo> threats = findAllThreats(board, color);

        for (ThreatInfo threat : threats) {
            score += calculateThreatScore(threat, color == playerColor);
        }

        // Bonus pour les menaces multiples
        if (threats.size() >= 2) {
            score += FORK_THREAT_SCORE * (threats.size() - 1);
        }

        return score;
    }

    private int evaluatePotentialThreats(Board board, ColorPawn color) {
        int score = 0;
        int boardSize = board.getSize();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                if (board.getToken(pos) == null) {
                    score += evaluateEmptyPosition(board, pos, color);
                }
            }
        }

        return score;
    }

    private List<ThreatInfo> findAllThreats(Board board, ColorPawn color) {
        List<ThreatInfo> threats = new ArrayList<>();
        int boardSize = board.getSize();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                Pawn pawn = BoardUtils.isPawn(pos, board);

                if (pawn != null && pawn.getColor() == color) {
                    analyzeThreatDirections(board, pos, pawn, threats);
                }
            }
        }

        return threats;
    }

    private void analyzeThreatDirections(Board board, Position pos, Pawn pawn, List<ThreatInfo> threats) {
        for (Direction dir : Direction.values()) {
            ThreatInfo threat = analyzeThreatInDirection(board, pos, pawn, dir);
            if (threat != null) {
                threats.add(threat);
            }
        }
    }

    private ThreatInfo analyzeThreatInDirection(Board board, Position pos, Pawn pawn, Direction dir) {
        int consecutive = LineCounter.countConsecutiveSameColor(board, pos, dir.getDeltaX(), dir.getDeltaY(), pawn.getColor());
        int emptySpaces = LineCounter.countEmptySpacesForPotentialWin(board, pos, dir.getDeltaX(), dir.getDeltaY(), pawn.getColor());

        if (consecutive >= 2) {
            return new ThreatInfo(consecutive, emptySpaces, dir, pos);
        }
        return null;
    }

    private int calculateThreatScore(ThreatInfo threat, boolean isPlayerThreat) {
        int baseScore = 0;

        // Menace immédiate de victoire
        if (threat.consecutive() >= 3 && threat.emptySpaces() > 0) {
            baseScore += WIN_SCORE;
        }
        // Menace forte
        else if (threat.consecutive() >= 2 && threat.emptySpaces() >= 2) {
            baseScore += DANGEROUS_ALIGNMENT_SCORE;
        }
        // Menace potentielle
        else if (threat.consecutive() >= 2 && threat.emptySpaces() == 1) {
            baseScore += POTENTIAL_ALIGNMENT_SCORE;
        }

        return isPlayerThreat ? baseScore : -baseScore;
    }

    private int evaluateEmptyPosition(Board board, Position pos, ColorPawn color) {
        int score = 0;
        Pawn virtualPawn = new Pawn(color, Mark.X); // Mark arbitraire pour l'évaluation

        // Évaluation des alignements potentiels
        for (Direction dir : Direction.values()) {
            int consecutive = LineCounter.countConsecutiveSameColor(board, pos, dir.getDeltaX(), dir.getDeltaY(), color);
            if (consecutive >= 2) {
                score += POTENTIAL_ALIGNMENT_SCORE * (consecutive - 1);
            }
        }

        // Bonus pour position stratégique
        if (isStrategicPosition(board, pos)) {
            score += PATTERN_BONUS;
        }

        return score;
    }

    private boolean isStrategicPosition(Board board, Position pos) {
        int center = board.getSize() / 2;
        int distanceToCenter = Math.abs(pos.row() - center) + Math.abs(pos.column() - center);

        // Position proche du centre
        if (distanceToCenter <= 1) {
            return true;
        }

        // Position permettant de créer des menaces multiples
        int potentialThreats = countPotentialThreatsFromPosition(board, pos);
        return potentialThreats >= 2;
    }

    private int countPotentialThreatsFromPosition(Board board, Position pos) {
        int threats = 0;
        for (Direction dir : Direction.values()) {
            if (canCreateThreatInDirection(board, pos, dir)) {
                threats++;
            }
        }
        return threats;
    }

    private boolean canCreateThreatInDirection(Board board, Position pos, Direction dir) {
        int consecutive = 0;
        int emptySpaces = 0;
        int x = pos.row();
        int y = pos.column();
        int size = board.getSize();

        // Vérification dans les deux directions
        for (int factor : new int[]{1, -1}) {
            int dx = dir.getDeltaX() * factor;
            int dy = dir.getDeltaY() * factor;
            int newX = x + dx;
            int newY = y + dy;

            while (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                Position newPos = new Position(newX, newY);
                Pawn pawn = BoardUtils.isPawn(newPos, board);

                if (pawn == null) {
                    emptySpaces++;
                    if (emptySpaces > 2) break;
                } else if (pawn.getColor() == playerColor) {
                    consecutive++;
                } else {
                    break;
                }

                newX += dx;
                newY += dy;
            }
        }

        return consecutive >= 1 && emptySpaces >= 1;
    }

    private record ThreatInfo(int consecutive, int emptySpaces, Direction direction, Position position) {}


    public int checkTwoColorAlignmentRisk(Board board, Position pos, Pawn pawn) {
        int risk = 0;
        for (Direction dir : Direction.values()) {
            if (isRiskyAlignment(board, pos, dir, pawn)) {
                risk += 2;
            }
        }
        return risk;
    }

    private boolean isRiskyAlignment(Board board, Position pos, Direction dir, Pawn pawn) {
        int consecutive = LineCounter.countConsecutiveSameColor(board, pos, dir.getDeltaX(), dir.getDeltaY(), pawn.getColor());
        int emptySpaces = LineCounter.countEmptySpacesForPotentialWin(board, pos, dir.getDeltaX(), dir.getDeltaY(), pawn.getColor());
        return consecutive >= 3 && emptySpaces >= 1;
    }

    public int evaluateMarkAlignment(Board board, Position pos, Mark mark) {
        return LineCounter.countAlignedMarks(board, pos, mark) * evaluator.getDangerousAlignmentScore();
    }
}