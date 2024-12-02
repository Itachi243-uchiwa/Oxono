package dev3.projet.oxono_g63888.model.strategy.search;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.strategy.Move;
import dev3.projet.oxono_g63888.model.strategy.TranspositionTable;
import dev3.projet.oxono_g63888.model.strategy.evaluation.*;

import java.util.List;

/**
 * Moteur de recherche implémentant l'algorithme NegaScout
 */
public class SearchEngine {
    private final ColorPawn aiColor;
    private final TranspositionTable transpositionTable;
    private final BoardEvaluator boardEvaluator;
    private final MoveGenerator moveGenerator;
    private int nodesExplored;

    public SearchEngine(ColorPawn aiColor) {
        this.aiColor = aiColor;
        this.transpositionTable = new TranspositionTable();
        this.boardEvaluator = new BoardEvaluator(aiColor);
        this.moveGenerator = new MoveGenerator();
        this.nodesExplored = 0;
    }

    public SearchResult findBestMove(Board board, AIPlayer ai, int maxDepth) {
        nodesExplored = 0;
        return iterativeDeepeningNegaScout(board, ai, maxDepth);
    }

    public int getNodesExplored() {
        return nodesExplored;
    }

    private SearchResult iterativeDeepeningNegaScout(Board board, AIPlayer ai, int maxDepth) {
        SearchResult bestResult = new SearchResult(Integer.MIN_VALUE, null, null, false);
        int currentDepth = 1;
        long startTime = System.currentTimeMillis();
        long timeLimit = 20000; // 20 secondes

        while (currentDepth <= maxDepth) {
            SearchResult result = negaScout(board, ai, currentDepth, 
                                          Integer.MIN_VALUE, Integer.MAX_VALUE, true);

            if (result.score() > bestResult.score()) {
                bestResult = result;
            }

            if (System.currentTimeMillis() - startTime > timeLimit) {
                break;
            }

            currentDepth++;
        }

        return bestResult;
    }

    private SearchResult negaScout(Board board, AIPlayer ai, int depth, int alpha, int beta, 
                                 boolean maximizingPlayer) {
        nodesExplored++;

        // Vérification de la table de transposition
        long zobristHash = board.calculateZobristHash();
        TranspositionTable.Entry entry = transpositionTable.probe(zobristHash);
        if (entry != null && entry.depth() >= depth) {
            return new SearchResult(entry.score(), entry.moveTotem(), 
                                  entry.movePawn(), false);
        }

        // Cas de base
        if (depth == 0) {
            int score = boardEvaluator.evaluatePosition(board);
            transpositionTable.store(zobristHash, depth, score, null, null);
            return new SearchResult(score, null, null, false);
        }

        List<Move[]> moves = moveGenerator.generateMoves(board, ai, aiColor);
        if (moves.isEmpty()) {
            return new SearchResult(boardEvaluator.evaluatePosition(board), 
                                  null, null, false);
        }

        Move bestTotemMove = null;
        Move bestPawnMove = null;
        int bestScore = Integer.MIN_VALUE;
        int b = beta;

        // Premier nœud
        Board firstBoard = board.copy();
        moveGenerator.applyMoves(firstBoard, moves.get(0)[0], moves.get(0)[1]);
        SearchResult firstResult = negaScout(firstBoard, ai, depth - 1, -b, -alpha, 
                                           !maximizingPlayer);
        bestScore = -firstResult.score();
        bestTotemMove = moves.get(0)[0];
        bestPawnMove = moves.get(0)[1];

        if (bestScore > alpha) {
            alpha = bestScore;
        }
        if (alpha >= beta) {
            transpositionTable.store(zobristHash, depth, bestScore, 
                                   bestTotemMove, bestPawnMove);
            return new SearchResult(bestScore, bestTotemMove, bestPawnMove, false);
        }

        // Reste des nœuds
        for (int i = 1; i < moves.size(); i++) {
            Board currentBoard = board.copy();
            moveGenerator.applyMoves(currentBoard, moves.get(i)[0], moves.get(i)[1]);

            int score;
            // Recherche avec fenêtre nulle
            SearchResult nullWindowResult = negaScout(currentBoard, ai, depth - 1, 
                                                    -alpha - 1, -alpha, !maximizingPlayer);
            score = -nullWindowResult.score();

            // Re-recherche si nécessaire
            if (alpha < score && score < beta) {
                SearchResult fullResult = negaScout(currentBoard, ai, depth - 1, 
                                                  -beta, -score, !maximizingPlayer);
                score = -fullResult.score();
            }

            if (score > bestScore) {
                bestScore = score;
                bestTotemMove = moves.get(i)[0];
                bestPawnMove = moves.get(i)[1];

                if (score > alpha) {
                    alpha = score;
                }
            }

            if (alpha >= beta) {
                break;
            }

            b = alpha + 1;
        }

        transpositionTable.store(zobristHash, depth, bestScore, bestTotemMove, bestPawnMove);
        return new SearchResult(bestScore, bestTotemMove, bestPawnMove, false);
    }
}