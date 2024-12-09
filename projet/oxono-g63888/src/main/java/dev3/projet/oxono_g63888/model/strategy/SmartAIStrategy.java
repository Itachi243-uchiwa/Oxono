package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.strategy.evaluator.*;
import java.util.*;

public class SmartAIStrategy implements AIStrategy {
    private static final int MAX_DEPTH = 4;
    private final ColorPawn color;
    private Move moveTotem;
    private Move movePawn;
    private final TranspositionTable transpositionTable;
    private int recursiveCalls;
    private final BoardEvaluator evaluator;
    private final ThreatAnalyzer threatAnalyzer;

    public SmartAIStrategy(ColorPawn color) {
        this.color = color;
        this.moveTotem = null;
        this.movePawn = null;
        this.recursiveCalls = 0;
        this.transpositionTable = new TranspositionTable();
        this.evaluator = new BoardEvaluator(color);
        this.threatAnalyzer = new ThreatAnalyzer(color, evaluator);
    }

    @Override
    public Move getNextMove(Board board) {
        return movePawn;
    }

    @Override
    public Move getNextTotemMove(Board board, AIPlayer ai) {
        long startTime = System.currentTimeMillis();
        SearchResult result = iterativeDeepeningSearch(board, ai);
        int resultScore = result.score() / recursiveCalls;

        SearchResult opponentBlockScore = simulateOpponentBlock(board, ai);
        int scoreOpp = opponentBlockScore.score();

        if (scoreOpp > resultScore && !opponentBlockScore.isThree()) {
            moveTotem = opponentBlockScore.totemMove();
            movePawn = opponentBlockScore.pawnMove();
        } else {
            moveTotem = result.totemMove();
            movePawn = result.pawnMove();
        }

        logPerformanceMetrics(startTime);
        return moveTotem;
    }


    private SearchResult iterativeDeepeningSearch(Board board, AIPlayer ai) {
        SearchResult bestResult = new SearchResult(Integer.MIN_VALUE, null, null, false);
        int currentDepth = 1;
        long startTime = System.currentTimeMillis();
        long timeLimit = 20000;

        while (currentDepth <= MAX_DEPTH && System.currentTimeMillis() - startTime <= timeLimit) {
            SearchResult result = negamaxSearch(board, ai, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            if (result.score() > bestResult.score()) {
                bestResult = result;
            }
            currentDepth++;
        }

        return bestResult;
    }

    private SearchResult negamaxSearch(Board board, AIPlayer ai, int depth, int alpha, int beta, boolean maximizingPlayer) {
        recursiveCalls++;

        TranspositionTable.Entry entry = checkTranspositionTable(board, depth);
        if (entry != null) {
            return new SearchResult(entry.score(), entry.moveTotem(), entry.movePawn(), false);
        }

        if (depth == 0) {
            int score = evaluator.evaluate(board);
            transpositionTable.store(board.calculateZobristHash(), depth, score, null, null);
            return new SearchResult(score, null, null, false);
        }

        List<Move[]> possibleMoves = generateAndSortMoves(board, ai);
        if (possibleMoves.isEmpty()) {
            return new SearchResult(evaluator.evaluate(board), null, null, false);
        }

        return findBestMove(board, possibleMoves, ai, depth, alpha, beta, maximizingPlayer);
    }

    private TranspositionTable.Entry checkTranspositionTable(Board board, int depth) {
        long zobristHash = board.calculateZobristHash();
        TranspositionTable.Entry entry = transpositionTable.probe(zobristHash);
        return (entry != null && entry.depth() >= depth) ? entry : null;
    }

    private List<Move[]> generateAndSortMoves(Board board, AIPlayer ai) {
        List<Move[]> moves = new ArrayList<>();
        for (Mark mark : Mark.values()) {
            if (!ai.hasPawn(mark)) continue;
            generateMovesForMark(board, mark, moves);
        }
        sortMovesByEvaluation(moves, board);
        return moves;
    }

    private void generateMovesForMark(Board board, Mark mark, List<Move[]> moves) {
        Position currentPos = board.getTotemPosition(mark);
        Totem totem = board.getTotem(currentPos);
        List<Position> totemMoves = board.getMovesPossibles(currentPos);

        for (Position totemPos : totemMoves) {
            Board tempBoard = board.copy();
            tempBoard.moveTotem(totem, totemPos);
            generatePawnMovesForTotemPosition(tempBoard, totemPos, mark, moves, totem);
        }
    }

    private void generatePawnMovesForTotemPosition(Board board, Position totemPos, Mark mark, List<Move[]> moves, Totem totem) {
        List<Position> pawnMoves = board.getInsertionPositions(totemPos);
        for (Position pawnPos : pawnMoves) {
            moves.add(new Move[]{
                new Move(totem, totemPos),
                new Move(new Pawn(color, mark), pawnPos)
            });
        }
    }

    private void sortMovesByEvaluation(List<Move[]> moves, Board board) {
        moves.sort((m1, m2) -> {
            Board b1 = board.copy();
            Board b2 = board.copy();
            BoardUtils.applyMoves(b1, m1[0], m1[1]);
            BoardUtils.applyMoves(b2, m2[0], m2[1]);
            return Integer.compare(evaluator.evaluate(b2), evaluator.evaluate(b1));
        });
    }

    private SearchResult findBestMove(Board board, List<Move[]> moves, AIPlayer ai, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int bestScore = Integer.MIN_VALUE;
        Move bestTotemMove = null;
        Move bestPawnMove = null;

        for (Move[] movables : moves) {
            Board boardCopy = board.copy();
            BoardUtils.applyMoves(boardCopy, movables[0], movables[1]);

            if (BoardUtils.isWinningMove(boardCopy, movables[1])) {
                transpositionTable.store(board.calculateZobristHash(), depth, evaluator.getWinScore(), movables[0], movables[1]);
                return new SearchResult(evaluator.getWinScore(), movables[0], movables[1], false);
            }

            SearchResult result = negamaxSearch(boardCopy, ai, depth - 1, -beta, -alpha, !maximizingPlayer);
            int score = -result.score();

            if (score > bestScore) {
                bestScore = score;
                bestTotemMove = movables[0];
                bestPawnMove = movables[1];
                alpha = Math.max(alpha, score);
                if (alpha >= beta) break;
            }
        }

        transpositionTable.store(board.calculateZobristHash(), depth, bestScore, bestTotemMove, bestPawnMove);
        return new SearchResult(bestScore, bestTotemMove, bestPawnMove, false);
    }

    private SearchResult simulateOpponentBlock(Board board, AIPlayer ai) {
        ColorPawn opponentColor = color == ColorPawn.BLACK ? ColorPawn.PINK : ColorPawn.BLACK;
        int bestBlockScore = Integer.MIN_VALUE;
        SearchResult bestBlockMove = new SearchResult(bestBlockScore, null, null, false);

        for (Mark mark : Mark.values()) {
            if (!ai.hasPawn(mark)) continue;

            SearchResult blockMove = findBestBlockingMove(board, mark, opponentColor, ai);
            if (blockMove.score() > bestBlockScore) {
                bestBlockScore = blockMove.score();
                bestBlockMove = blockMove;
            }
        }

        return bestBlockMove;
    }

    private SearchResult findBestBlockingMove(Board board, Mark mark, ColorPawn opponentColor, AIPlayer ai) {
        Position currentPosition = board.getTotemPosition(mark);
        Totem totem = board.getTotem(currentPosition);
        List<Position> totemMoves = board.getMovesPossibles(currentPosition);

        int bestScore = Integer.MIN_VALUE;
        SearchResult bestMove = new SearchResult(bestScore, null, null, false);

        for (Position totemPos : totemMoves) {
            SearchResult moveResult = evaluateBlockingPosition(board, totem, totemPos, mark, opponentColor);
            if (moveResult.score() > bestScore) {
                bestScore = moveResult.score();
                bestMove = moveResult;
            }
        }

        return bestMove;
    }

    private SearchResult evaluateBlockingPosition(Board board, Totem totem, Position totemPos, Mark mark, ColorPawn opponentColor) {
        Board boardCopy = board.copy();
        boardCopy.moveTotem(totem, totemPos);

        List<Position> pawnPositions = boardCopy.getInsertionPositions(totemPos);
        int bestScore = Integer.MIN_VALUE;
        boolean isThreeAligned = false;
        Move bestTotemMove = new Move(totem, totemPos);
        Move bestPawnMove = null;

        for (Position pawnPos : pawnPositions) {
            Pawn pawn = new Pawn(opponentColor, mark);
            boardCopy.insertPawn(pawn, pawnPos, totemPos);

            int score = calculateBlockingScore(boardCopy, pawn, pawnPos, mark);
            if (score > bestScore) {
                bestScore = score;
                bestPawnMove = new Move(new Pawn(color, mark), pawnPos);
                isThreeAligned = threatAnalyzer.evaluateMarkAlignment(boardCopy, pawnPos, mark) > 0;
            }

            boardCopy.removePawn(pawnPos);
        }

        return new SearchResult(bestScore, bestTotemMove, bestPawnMove, isThreeAligned);
    }

    private int calculateBlockingScore(Board board, Pawn pawn, Position pawnPos, Mark mark) {
        int score = 0;

        if (board.checkWin(pawn, pawnPos)) {
            score += evaluator.getBlockWinScore();
        } else if (threatAnalyzer.checkTwoColorAlignmentRisk(board, pawnPos, pawn) >= 2) {
            score += evaluator.getTwoColorAlignmentRisk();
            score -= threatAnalyzer.evaluateMarkAlignment(board, pawnPos, mark);
            score += threatAnalyzer.analyzeThreats(board);
        }

        return score;
    }

    private void logPerformanceMetrics(long startTime) {
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        System.out.println("Nodes explored: " + recursiveCalls);
    }
}