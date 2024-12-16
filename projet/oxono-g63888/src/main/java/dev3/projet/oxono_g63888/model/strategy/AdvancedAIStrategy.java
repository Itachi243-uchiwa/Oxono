package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.*;

import java.util.*;

public class AdvancedAIStrategy implements AIStrategy {

    private static final int MAX_DEPTH = 3;
    private static final int WIN_SCORE = 1000000;
    private static final int LOSE_SCORE = -1000000;
    private AIPlayer ai;
    private final ColorPawn aiColor;
    private Move movePawn;
    private Move moveTotem;
    private TranspositionTable transpositionTable;
    private int recursiveCounter;

    public AdvancedAIStrategy(ColorPawn color) {
        this.aiColor = color;
        this.movePawn = null;
        this.moveTotem = null;
        this.transpositionTable = new TranspositionTable();
        this.recursiveCounter = 0;
    }

    /**
     * Determines and returns the next move for the AI player based on the current state of the game board.
     *
     * @param board The current game board, representing the state of the game with all pieces and positions.
     * @return The next move the AI player intends to make, represented as a Move object.
     */
    @Override
    public Move getNextMove(Board board) {
        return movePawn;
    }

    /**
     * Determines and returns the next move for the totem based on the current state of the game board.
     *
     * @param board the current state of the game board on which the strategy is applied.
     * @param aiPlayer the AI player for which the move is being calculated.
     * @return the calculated next move for the totem.
     */
    @Override
    public Move getNextTotemMove(Board board, AIPlayer aiPlayer) {
        ai = aiPlayer;
        getBestMove(board);
        System.out.println(recursiveCounter);
        return moveTotem;
    }

    /**
     * Determines and selects the best possible move for the AI player based on the current state of the board.
     * This method utilizes the minimax algorithm with alpha-beta pruning to evaluate potential moves and
     * incorporates randomness to avoid predictable behavior.
     *
     * @param board The current state of the game board on which the AI will analyze and determine the best move.
     */
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



    /**
     * Implements the minimax algorithm with alpha-beta pruning to evaluate the best possible move
     * for AI in a game. Uses a transposition table to avoid re-evaluating previously encountered states.
     *
     * @param board the current state of the game board
     * @param depth the remaining depth to explore in the game tree
     * @param alpha the alpha (best already guaranteed score for maximizer) value for pruning
     * @param beta the beta (best already guaranteed score for minimizer) value for pruning
     * @param isMaximizing a flag indicating if the current step is for the maximizing player
     * @return the best score attainable from the current state of the board within the given depth
     */
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

/**
 * Generates a list of all possible moves in the current board state
 * for a player using the AI's assigned color and available pawns.
 *
 * @param board The current state of the game board.
 * @return A list of all possible moves, each represented as a CompleteMove object.
 */
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

    /**
     * Checks if the AI player has at least one pawn of the specified mark.
     *
     * @param mark The mark (Mark.X or Mark.O) to check for.
     * @return true if the AI player has at least one pawn of the specified mark, false otherwise.
     */
    private boolean hasPawnOfMark(Mark mark) {
        return ai.hasPawn(mark);
    }

    /**
     * Applies the given move to the board by moving the specified totem and placing a new pawn.
     *
     * @param board The game board where the move is applied.
     * @param move The complete move to be applied, including totem and pawn actions.
     * @param color The color of the pawn to be placed as part of the move.
     */
    private void applyMove(Board board, CompleteMove move, ColorPawn color) {
        Totem totem = (Totem) move.totemMove.token();
        Pawn pawn = new Pawn(color, totem.getMark());
        board.moveTotem(totem, move.totemMove.movePosition());
        board.insertPawn(pawn, move.pawnMove.movePosition(), move.totemMove.movePosition());
    }

    /**
     * Determines if the game is over based on the current state of the board.
     * The game is considered over if a winning condition is met for a pawn
     * or if there are no more possible moves left on the board.
     *
     * @param board the game board to check for the game-over condition.
     * @return true if the game is over, otherwise false.
     */
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

    /**
     * Evaluates the current state of the game board by calculating a score
     * based on various strategic factors such as alignments, totem positions,
     * center control, and mobility.
     *
     * @param board the game board to evaluate
     * @return an integer representing the computed score of the board state,
     *         which reflects the strategic advantage of the board configuration
     */
    private int evaluateBoard(Board board) {
        int score = 0;

        score += evaluateAlignments(board);
        score += evaluateTotemPositions(board);
        score += evaluateCenterControl(board);
        score += evaluateMobility(board);

        return score;
    }

    /**
     * Evaluates the alignments of pawns on the board, determining a score based on game state.
     *
     * @param board the game board on which the evaluation is performed
     * @return an integer score representing the evaluation of the alignments, with potential high or low values for winning or losing conditions
     */
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

    /**
     * Evaluates the alignment of a specific pawn on the board from a given position
     * in all possible directions and computes a score based on the alignment pattern.
     *
     * @param board The current state of the game board to evaluate.
     * @param pawn The specific pawn to be evaluated for alignment.
     * @param pos The position of the pawn on the board to start the evaluation from.
     * @return An integer score representing the alignment's contribution to the overall score.
     */
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

    /**
     * Counts the number of consecutive pawns in a specific direction starting from a given position
     * that have the same mark as the provided pawn.
     *
     * @param board the game board on which the pawns are placed
     * @param pawn the pawn whose mark will be used for comparison
     * @param pos the starting position to begin counting consecutive pawns
     * @param dir the direction in which to count the pawns
     * @return the number of consecutive pawns in the specified direction with the same mark as the given pawn
     */
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

    /**
     * Evaluates the positions of totems on the board and calculates a score based on their proximity
     * to the central area of the board. The score increases the closer a totem is to the center.
     *
     * @param board the board object representing the current state of the game
     * @return the calculated score reflecting the positioning of the totems
     */
    private int evaluateTotemPositions(Board board) {
        int score = 0;
        for (Mark mark : Mark.values()) {
            Position totemPos = board.getTotemPosition(mark);
            score += (board.getSize() / 2 - Math.abs(totemPos.row() - board.getSize() / 2)) * 5;
            score += (board.getSize() / 2 - Math.abs(totemPos.column() - board.getSize() / 2)) * 5;
        }
        return score;
    }

    /**
     * Evaluates the control over the central area of the board by calculating
     * a score based on the presence of pawns in the center region. Positive scores
     * are given for AI-controlled pawns, while negative scores are given for opponent pawns.
     *
     * @param board The current game board to evaluate.
     * @return An integer score representing the AI's control of the center area.
     *         Higher scores indicate stronger control.
     */
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

    /**
     * Evaluates the mobility of all marks on the board by calculating possible moves and insert positions
     * for each mark's totem. Mobility is an indicator of the gameplay flexibility provided by the current
     * board state.
     *
     * @param board The game board containing the current state of the game, including positions of marks and totems.
     * @return An integer score representing the total mobility for all marks. Higher mobility translates to a higher score.
     */
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

