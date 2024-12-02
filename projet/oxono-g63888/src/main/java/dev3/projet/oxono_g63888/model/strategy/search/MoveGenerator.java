package dev3.projet.oxono_g63888.model.strategy.search;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.strategy.Move;

import java.util.*;

/**
 * Générateur de coups possibles
 */
public class MoveGenerator {
    
    /**
     * Génère tous les coups possibles pour un joueur
     */
    public List<Move[]> generateMoves(Board board, AIPlayer ai, ColorPawn color) {
        List<Move[]> moves = new ArrayList<>();

        for (Mark mark : Mark.values()) {
            if (!ai.hasPawn(mark)) {
                continue;
            }

            Position currentPos = board.getTotemPosition(mark);
            Totem totem = board.getTotem(currentPos);
            List<Position> totemMoves = board.getMovesPossibles(currentPos);

            for (Position totemPos : totemMoves) {
                Board tempBoard = board.copy();
                tempBoard.moveTotem(totem, totemPos);
                List<Position> pawnMoves = tempBoard.getInsertionPositions(totemPos);

                for (Position pawnPos : pawnMoves) {
                    moves.add(new Move[]{
                        new Move(totem, totemPos),
                        new Move(new Pawn(color, mark), pawnPos)
                    });
                }
            }
        }

        // Trie les coups selon leur évaluation préliminaire
        sortMoves(moves, board);
        return moves;
    }

    /**
     * Applique un coup sur le plateau
     */
    public void applyMoves(Board board, Move totemMove, Move pawnMove) {
        board.moveTotem((Totem)totemMove.token(), totemMove.movePosition());
        board.insertPawn((Pawn)pawnMove.token(), pawnMove.movePosition(), 
                        totemMove.movePosition());
    }

    /**
     * Trie les coups selon leur potentiel stratégique
     */
    private void sortMoves(List<Move[]> moves, Board board) {
        moves.sort((m1, m2) -> {
            Board b1 = board.copy();
            Board b2 = board.copy();
            applyMoves(b1, m1[0], m1[1]);
            applyMoves(b2, m2[0], m2[1]);
            return Integer.compare(
                evaluateMovePotential(b2, m2[1].movePosition()),
                evaluateMovePotential(b1, m1[1].movePosition())
            );
        });
    }

    /**
     * Évalue rapidement le potentiel d'un coup
     */
    private int evaluateMovePotential(Board board, Position pos) {
        int score = 0;
        
        // Bonus pour les positions centrales
        int center = board.getSize() / 2;
        int distanceToCenter = Math.abs(pos.row() - center) + Math.abs(pos.column() - center);
        score += 10 / (distanceToCenter + 1);

        // Bonus pour les alignements potentiels
        score += evaluateAlignmentPotential(board, pos);

        return score;
    }

    /**
     * Évalue le potentiel d'alignement d'une position
     */
    private int evaluateAlignmentPotential(Board board, Position pos) {
        int score = 0;
        int size = board.getSize();

        // Vérifie les alignements horizontaux, verticaux et diagonaux
        int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}};
        
        for (int[] dir : directions) {
            int count = 1;
            int row = pos.row();
            int col = pos.column();

            // Vérifie dans les deux directions
            for (int factor : new int[]{1, -1}) {
                int r = row + dir[0] * factor;
                int c = col + dir[1] * factor;
                
                while (r >= 0 && r < size && c >= 0 && c < size) {
                    Token token = board.getToken(new Position(r, c));
                    if (token instanceof Pawn) {
                        count++;
                    }
                    r += dir[0] * factor;
                    c += dir[1] * factor;
                }
            }

            score += count * 5;
        }

        return score;
    }
}