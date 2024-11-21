package dev3.projet.oxono_g63888.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {


    @Test
    public void testInitializeBoard() {
        Board board = new Board(5);
        assertEquals(Mark.X, board.getPositionToken(board.getTotemPosition(Mark.X)).getMark());
        assertEquals(Mark.O, board.getPositionToken(board.getTotemPosition(Mark.O)).getMark());
    }

    @Test
    public void testMoveTotemValid() {
        Board board = new Board(5);
        Position oldPosition = board.getTotemPosition(Mark.X);
        Position newPosition = new Position(1, 2);

        board.moveTotem(new Totem(Mark.X), newPosition);

        assertEquals(newPosition, board.getTotemPosition(Mark.X));
        assertNull(board.getToken(oldPosition));
        assertNotNull(board.getToken(newPosition));
    }

    @Test
    public void testMoveTotemInvalid() {
        Board board = new Board(5);
        Position invalidPosition = new Position(6, 6);
        assertThrows(OxonoException.class, () -> {
            board.moveTotem(new Totem(Mark.X), invalidPosition);
        });
    }

    @Test
    public void testInsertPawnValid() {
        Board board = new Board(6);
        Position totemPosition = board.getTotemPosition(Mark.X);
        Position pawnPosition = new Position(2, 3);

        Pawn pawn = new Pawn(ColorPawn.BLACK, Mark.X);
        board.insertPawn(pawn, pawnPosition, totemPosition);

        assertNotNull(board.getToken(pawnPosition));
        assertEquals(pawn, board.getToken(pawnPosition));
    }

    @Test
    public void testInsertPawnInvalid() {
        Board board = new Board(5);
        Position invalidPosition = new Position(6, 6);  // Hors des limites
        Position totemPosition = board.getTotemPosition(Mark.X);

        Pawn pawn = new Pawn( ColorPawn.PINK, Mark.X);
        assertThrows(OxonoException.class, () -> {
            board.insertPawn(pawn, invalidPosition, totemPosition);
        });
    }

    @Test
    public void testCheckWinHorizontal() {
        Board board = new Board(6);
        Totem totem = new Totem(Mark.X);

        board.insertPawn(new Pawn(ColorPawn.PINK, Mark.X), new Position(1, 2), board.getTotemPosition(Mark.X));
        board.moveTotem(totem, new Position(2, 3));
        board.insertPawn(new Pawn(ColorPawn.BLACK, Mark.X), new Position(1, 3), board.getTotemPosition(Mark.X));
        board.moveTotem(totem, new Position(2, 4));
        board.insertPawn(new Pawn(ColorPawn.BLACK, Mark.X), new Position(1, 4), board.getTotemPosition(Mark.X));
        board.moveTotem(totem, new Position(2, 5));
        board.insertPawn(new Pawn(ColorPawn.PINK, Mark.X), new Position(1, 5), board.getTotemPosition(Mark.X));

        assertTrue(board.checkWin(new Pawn(ColorPawn.PINK, Mark.X), new Position(1, 3)));
    }


    @Test
    public void testCheckWinVertical() {
        Board board = new Board(6);
        Totem totemX = new Totem(Mark.X);
        Totem totemO = new Totem(Mark.O);

        board.insertPawn(new Pawn(ColorPawn.PINK, Mark.X), new Position(2,1 ), board.getTotemPosition(Mark.X));
        board.moveTotem(totemO, new Position(3, 2));
        board.insertPawn(new Pawn(ColorPawn.PINK, Mark.O), new Position(3, 1), board.getTotemPosition(Mark.O));
        board.moveTotem(totemX, new Position(1, 2));
        board.insertPawn(new Pawn(ColorPawn.PINK, Mark.X), new Position(1, 1), board.getTotemPosition(Mark.X));
        board.moveTotem(totemO, new Position(4, 2));
        board.insertPawn(new Pawn(ColorPawn.PINK, Mark.O), new Position(4, 1), board.getTotemPosition(Mark.O));

        assertTrue(board.checkWin(new Pawn(ColorPawn.PINK, Mark.X), new Position(2, 1)));
    }

    @Test
    public void testGetMovesPossibles() {
        Board board = new Board(5);
        Position totemPosition = board.getTotemPosition(Mark.X);

        List<Position> moves = board.getMovesPossibles(totemPosition);
        assertFalse(moves.isEmpty());
    }

    @Test
    public void testIsValidMove() {
        Board board = new Board(6);
        Position totemPosition = board.getTotemPosition(Mark.X);
        Position validMove = new Position(2, 3);
        Position invalidMove = new Position(6, 6);  // Hors des limites

        assertTrue(board.isValidMove(validMove, totemPosition));
        assertThrows(OxonoException.class, () -> {
            board.isValidMove(invalidMove, totemPosition);
        });
    }

    @Test
    public void testGetAdjacentCells() {
        Board board = new Board(5);
        Position totemPosition = board.getTotemPosition(Mark.X);

        List<Position> adjacentCells = board.getAdjacentCells(totemPosition);
        assertFalse(adjacentCells.isEmpty());
    }

}