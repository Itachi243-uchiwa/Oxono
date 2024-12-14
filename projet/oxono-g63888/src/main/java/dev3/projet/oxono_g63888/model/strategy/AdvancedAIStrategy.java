package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.*;

import java.util.*;

public class AdvancedAIStrategy implements AIStrategy {

    private static final int MAX_DEPTH = 4;
    private static final int WIN_SCORE = 1000000;
    private static final int LOSE_SCORE = -1000000;
    private AIPlayer ai;
    private final ColorPawn aiColor;
    private Move movePawn;
    private Move moveTotem;
    private final TranspositionTable transpositionTable = new TranspositionTable();
    private int recursiveCounter;

    public AdvancedAIStrategy(ColorPawn color) {
        this.aiColor = color;
        this.movePawn = null;
        this.moveTotem = null;
        this.recursiveCounter = 0;
    }

    @Override
    public Move getNextMove(Board board) {
        return movePawn;
    }

    @Override
    public Move getNextTotemMove(Board board, AIPlayer aiPlayer) {
        ai = aiPlayer;
        getBestMove(board);
        System.out.println(recursiveCounter);
        return moveTotem;
    }

    private void getBestMove(Board board) {
        int bestScore = Integer.MIN_VALUE;

        List<CompleteMove> possibleMoves = generateMoves(board);
        Collections.shuffle(possibleMoves); // Ajoute de l'aléatoire pour éviter la prévisibilité

        for (CompleteMove move : possibleMoves) {
            Board newBoard = board.copy();
            applyMove(newBoard, move, aiColor);
            Move checkMove = move.poMove();

            if (newBoard.checkWin((Pawn) checkMove.token(), checkMove.movePosition())) {
                moveTotem = move.toMove();
                movePawn = move.poMove();
                return;
            }

            int score = minimax(newBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);


            if (score > bestScore) {
                bestScore = score;
                moveTotem = move.toMove();
                movePawn = move.poMove();
            }
        }

    }



    private int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        recursiveCounter++;
        long boardHash = board.calculateZobristHash();

        TranspositionTable.TranspositionEntry entry = transpositionTable.get(boardHash);
        if (entry != null && entry.depth >= depth) {
            if (entry.flag == TranspositionTable.TranspositionEntry.EXACT) {
                return entry.score;
            } else if (entry.flag == TranspositionTable.TranspositionEntry.LOWER_BOUND) {
                alpha = Math.max(alpha, entry.score);
            } else if (entry.flag == TranspositionTable.TranspositionEntry.UPPER_BOUND) {
                beta = Math.min(beta, entry.score);
            }
            if (alpha >= beta) {
                return entry.score;
            }
        }

        if (depth == 0 || isGameOver(board)) {
            return evaluateBoard(board);
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<CompleteMove> possibleMoves = generateMoves(board);

        for (CompleteMove move : possibleMoves) {
            Board newBoard = board.copy();
            applyMove(newBoard, move, aiColor);

            int score = minimax(newBoard, depth - 1, alpha, beta, !isMaximizing);

            if (isMaximizing) {
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, bestScore);
            } else {
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, bestScore);
            }

            if (beta <= alpha) {
                break;
            }
        }

        int flag = TranspositionTable.TranspositionEntry.EXACT;
        if (bestScore <= alpha) {
            flag = TranspositionTable.TranspositionEntry.UPPER_BOUND;
        } else if (bestScore >= beta) {
            flag = TranspositionTable.TranspositionEntry.LOWER_BOUND;
        }
        transpositionTable.put(boardHash, new TranspositionTable.TranspositionEntry(bestScore, depth, flag));

        return bestScore;
    }

private List<CompleteMove> generateMoves(Board board) {
        List<CompleteMove> moves = new ArrayList<>();
        for (Mark mark : Mark.values()) {
            if (hasPawnOfMark(mark)) {
                Position totemPos = board.getTotemPosition(mark);
                Totem totem = board.getTotem(totemPos);

                for (Position newTotemPos : board.getMovesPossibles(totemPos)) {
                    Board tempBoard = board.copy();
                    tempBoard.moveTotem(totem, newTotemPos);

                    for (Position pawnPos : tempBoard.getInsertionPositions(newTotemPos)) {

                            moves.add(new CompleteMove(
                                    new Move(totem, newTotemPos),
                                    new Move(new Pawn(aiColor, mark), pawnPos)
                            ));

                    }
                }
            }
        }
        return moves;
    }

    private boolean hasPawnOfMark(Mark mark) {
        return ai.hasPawn(mark);
    }

    private void applyMove(Board board, CompleteMove move, ColorPawn color) {
        Totem totem = (Totem) move.totemMove.token();
        Pawn pawn = new Pawn(color, totem.getMark());
        board.moveTotem(totem, move.totemMove.movePosition());
        board.insertPawn(pawn, move.pawnMove.movePosition(), move.totemMove.movePosition());
    }

    private boolean isGameOver(Board board) {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);
                if (token instanceof Pawn) {
                    if (board.checkWin((Pawn) token, pos)) {
                        return true;
                    }
                }
            }
        }
        return board.allPositionsEmpty().isEmpty();
    }

    private int evaluateBoard(Board board) {
        int score = 0;

        score += evaluateAlignments(board);
        score += evaluateTotemPositions(board);
        score += evaluateCenterControl(board);
        score += evaluateMobility(board);

        return score;
    }

    private int evaluateAlignments(Board board) {
        int score = 0;
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);
                if (token instanceof Pawn) {
                    Pawn pawn = (Pawn) token;
                    if (board.checkWin(pawn, pos)) {
                        return pawn.getColor() == aiColor ? WIN_SCORE : LOSE_SCORE;
                    }
                    score += evaluateAlignment(board, pawn, pos);
                }
            }
        }
        return score;
    }

    private int evaluateAlignment(Board board, Pawn pawn, Position pos) {
        int score = 0;
        for (Direction dir : Direction.values()) {
            int count = countConsecutivePawns(board, pawn, pos, dir);
            if (pawn.getColor() == aiColor && count <= 2) {
                score += (int) Math.pow(5, count);
            } else {
                score -= (int) Math.pow(5, count);
            }
        }
        return score;
    }

    private int countConsecutivePawns(Board board, Pawn pawn, Position pos, Direction dir) {
        int count = 1;
        Position current = pos;
        while (true) {
            current = new Position(current.row() + dir.getDeltaX(), current.column() + dir.getDeltaY());
            if (!board.isInBounds(current)) break;
            Token token = board.getToken(current);
            if (!(token instanceof Pawn)) break;
            Pawn currentPawn = (Pawn) token;
            if (currentPawn.getMark() != pawn.getMark()) break;
            count++;
        }
        return count;
    }

    private int evaluateTotemPositions(Board board) {
        int score = 0;
        for (Mark mark : Mark.values()) {
            Position totemPos = board.getTotemPosition(mark);
            score += (board.getSize() / 2 - Math.abs(totemPos.row() - board.getSize() / 2)) * 5;
            score += (board.getSize() / 2 - Math.abs(totemPos.column() - board.getSize() / 2)) * 5;
        }
        return score;
    }

    private int evaluateCenterControl(Board board) {
        int score = 0;
        int centerStart = board.getSize() / 4;
        int centerEnd = board.getSize() - centerStart;

        for (int row = centerStart; row < centerEnd; row++) {
            for (int col = centerStart; col < centerEnd; col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);
                if (token instanceof Pawn) {
                    Pawn pawn = (Pawn) token;
                    if (pawn.getColor() == aiColor) {
                        score += 5;
                    } else {
                        score -= 5;
                    }
                }
            }
        }
        return score;
    }

    private int evaluateMobility(Board board) {
       int score = 0;
        for (Mark mark : Mark.values()) {
            Position totemPos = board.getTotemPosition(mark);
            score += board.getMovesPossibles(totemPos).size() * 10;
            score += board.getInsertionPositions(totemPos).size() * 10;
        }
        return score;

    }

    private static class CompleteMove {
        final Move totemMove;
        final Move pawnMove;

        CompleteMove(Move totemMove, Move pawnMove) {
            this.totemMove = totemMove;
            this.pawnMove = pawnMove;
        }

        Move toMove() {
            return totemMove;
        }
        Move poMove() {
            return pawnMove;
        }
    }
}

