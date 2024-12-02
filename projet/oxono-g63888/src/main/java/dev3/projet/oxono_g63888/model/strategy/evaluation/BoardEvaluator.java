package dev3.projet.oxono_g63888.model.strategy.evaluation;

import dev3.projet.oxono_g63888.model.*;

/**
 * Évaluateur de plateau combinant différents aspects stratégiques
 */
public class BoardEvaluator {
    private final PositionalEvaluator positionalEvaluator;
    private final ThreatEvaluator threatEvaluator;
    private final MobilityEvaluator mobilityEvaluator;
    private final PatternEvaluator patternEvaluator;

    public BoardEvaluator(ColorPawn aiColor) {
        this.positionalEvaluator = new PositionalEvaluator(aiColor);
        this.threatEvaluator = new ThreatEvaluator(aiColor);
        this.mobilityEvaluator = new MobilityEvaluator(aiColor);
        this.patternEvaluator = new PatternEvaluator(aiColor);
    }

    /**
     * Évalue la position globale du plateau
     */
    public int evaluatePosition(Board board) {
        return positionalEvaluator.evaluate(board) +
               threatEvaluator.evaluate(board) +
               mobilityEvaluator.evaluate(board) +
               patternEvaluator.evaluate(board);
    }
}