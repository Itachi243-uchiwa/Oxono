package dev3.projet.oxono_g63888.model.strategy.evaluator;

import dev3.projet.oxono_g63888.model.*;

public class LineCounter {
    public static int countConsecutiveSameColor(Board board, Position startPos, int dx, int dy, ColorPawn color) {
        int count = 1;
        count += countInDirection(board, startPos, dx, dy, color);
        count += countInDirection(board, startPos, -dx, -dy, color);
        return count;
    }

    private static int countInDirection(Board board, Position startPos, int dx, int dy, ColorPawn color) {
        int count = 0;
        int x = startPos.row() + dx;
        int y = startPos.column() + dy;

        while (isValidPosition(x, y, board.getSize())) {
            Pawn pawn = BoardUtils.isPawn(new Position(x, y), board);
            if (pawn == null || pawn.getColor() != color) break;
            count++;
            x += dx;
            y += dy;
        }

        return count;
    }

    public static int countEmptySpacesForPotentialWin(Board board, Position startPos, int dx, int dy, ColorPawn color) {
        boolean forwardEmpty = hasEmptySpace(board, startPos, dx, dy, color);
        boolean backwardEmpty = hasEmptySpace(board, startPos, -dx, -dy, color);
        return (forwardEmpty && backwardEmpty) ? 1 : 0;
    }

    private static boolean hasEmptySpace(Board board, Position startPos, int dx, int dy, ColorPawn color) {
        int x = startPos.row() + dx;
        int y = startPos.column() + dy;

        while (isValidPosition(x, y, board.getSize())) {
            Position pos = new Position(x, y);
            if (board.getToken(pos) == null) return true;
            if (!(board.getToken(pos) instanceof Pawn pawn) || pawn.getColor() != color) break;
            x += dx;
            y += dy;
        }
        return false;
    }

    public static int countAlignedMarks(Board board, Position pos, Mark mark) {
        int score = 0;
        for (Direction dir : Direction.values()) {
            int count = 1 + countMarkInDirection(board, pos, dir.getDeltaX(), dir.getDeltaY(), mark, 0);
            if (count >= 3) score++;
        }
        return score;
    }

    private static int countMarkInDirection(Board board, Position startPos, int dx, int dy, Mark mark, int gaps) {
        int count = 0;
        int x = startPos.row() + dx;
        int y = startPos.column() + dy;

        while (isValidPosition(x, y, board.getSize()) && gaps <= 1) {
            Position pos = new Position(x, y);
            Pawn pawn = BoardUtils.isPawn(pos, board);

            if (pawn == null) {
                gaps++;
            } else if (pawn.getMark() == mark) {
                count++;
            } else {
                break;
            }

            x += dx;
            y += dy;
        }

        return count;
    }

    private static boolean isValidPosition(int x, int y, int size) {
        return x >= 0 && y >= 0 && x < size && y < size;
    }
}