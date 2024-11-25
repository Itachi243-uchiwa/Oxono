package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.Board;
import dev3.projet.oxono_g63888.model.Pawn;
import dev3.projet.oxono_g63888.model.Position;
import dev3.projet.oxono_g63888.model.Token;

import java.util.*;

public class MCTSNode {
    private Move move;
    private MCTSNode parent;
    private List<MCTSNode> children;
    private Board board;
    private List<Move> untriedMoves;
    private int visits;
    private double score;
    private Position totemPosition;
    private Position movePosition;

    public MCTSNode(Move move, MCTSNode parent, Board board, List<Move> untriedMoves) {
        this.move = move;
        this.parent = parent;
        this.board = board;
        this.untriedMoves = untriedMoves != null ? untriedMoves : new ArrayList<>();
        this.children = new ArrayList<>();
        this.visits = 0;
        this.score = 0;
    }

    public void addChild(MCTSNode child) {
        children.add(child);
    }

    public boolean isTerminal() {

        Board currentBoard = getBoard();
        return isGameOver(currentBoard) || getUntriedMoves().isEmpty();
    }

    private boolean isGameOver(Board board) {
        // Check for wins
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
        // Check if board is full
        return board.allPositionsEmpty().isEmpty();
    }

    public boolean isFullyExpanded() {
        return untriedMoves.isEmpty();
    }

    public void incrementVisits() {
        visits++;
    }

    public void updateScore(double result) {
        score += result;
    }
    public MCTSNode getParent() { return parent; }
    public List<MCTSNode> getChildren() { return children; }
    public Board getBoard() { return board; }
    public List<Move> getUntriedMoves() { return untriedMoves; }
    public int getVisits() { return visits; }
    public double getScore() { return score; }
    public Position getTotemPosition() { return totemPosition; }
    public Position getMovePosition() { return movePosition; }
}