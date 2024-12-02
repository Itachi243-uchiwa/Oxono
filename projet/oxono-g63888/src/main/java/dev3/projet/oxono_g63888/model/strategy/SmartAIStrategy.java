package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.strategy.evaluation.*;
import dev3.projet.oxono_g63888.model.strategy.search.*;
import java.util.*;

/**
 * Implémentation avancée de l'IA utilisant l'algorithme NegaScout
 */
public class SmartAIStrategy implements AIStrategy {
    private static final int MAX_DEPTH = 4;
    private final ColorPawn color;
    private Move moveTotem;
    private Move movePawn;
    private final SearchEngine searchEngine;
    private final MoveGenerator moveGenerator;

    public SmartAIStrategy(ColorPawn color) {
        this.color = color;
        this.moveTotem = null;
        this.movePawn = null;
        this.searchEngine = new SearchEngine(color);
        this.moveGenerator = new MoveGenerator();
    }

    @Override
    public Move getNextMove(Board board) {
        return movePawn;
    }

    @Override
    public Move getNextTotemMove(Board board, AIPlayer ai) {
        long startTime = System.currentTimeMillis();
        
        SearchResult result = searchEngine.findBestMove(board, ai, MAX_DEPTH);
        moveTotem = result.totemMove();
        movePawn = result.pawnMove();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Temps de calcul: " + (endTime - startTime) + "ms");
        System.out.println("Noeuds explorés: " + searchEngine.getNodesExplored());

        return moveTotem;
    }
}