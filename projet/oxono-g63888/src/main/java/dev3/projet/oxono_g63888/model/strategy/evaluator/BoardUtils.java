package dev3.projet.oxono_g63888.model.strategy.evaluator;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.strategy.Move;

public class BoardUtils {
    public static Pawn isPawn(Position pos, Board board) {
        Token token = board.getToken(pos);
        return token instanceof Pawn ? (Pawn) token : null;
    }

    public static boolean isWinningMove(Board board, Move pawnMove) {
        return board.checkWin((Pawn)pawnMove.token(), pawnMove.movePosition());
    }

    public static void applyMoves(Board board, Move totemMove, Move pawnMove) {
        board.moveTotem((Totem)totemMove.token(), totemMove.movePosition());
        board.insertPawn((Pawn)pawnMove.token(), pawnMove.movePosition(), totemMove.movePosition());
    }
}