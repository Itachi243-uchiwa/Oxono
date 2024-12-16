package dev3.projet.oxono_g63888.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {


    /**
     * Tests the initialization of the game board to verify that the starting positions
     * of the totems for each mark are correctly set.
     *
     * The method creates a game board instance of a specific size and checks if the
     * positions for the marks 'X' and 'O' are initialized correctly. It asserts that
     * the marks at their respective totem positions match the expected value.
     */
    @Test
    public void testInitializeBoard() {
        Board board = new Board(5);
        assertEquals(Mark.X, board.getPositionToken(board.getTotemPosition(Mark.X)).getMark());
        assertEquals(Mark.O, board.getPositionToken(board.getTotemPosition(Mark.O)).getMark());
    }

    /**
     * Tests the functionality of moving a totem to a valid new position on the board.
     *
     * Verifies that the new position of the totem matches the expected position,
     * that the old position no longer contains the totem, and that the new position
     * has been updated to contain the totem after the move.
     *
     * Ensures the game's board state is updated correctly when a totem move is valid.
     */
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

    /**
     * Tests the scenario where attempting to move a totem to an invalid position on the board.
     * Ensures that an OxonoException is thrown when the position is outside the board's boundaries.
     */
    @Test
    public void testMoveTotemInvalid() {
        Board board = new Board(5);
        Position invalidPosition = new Position(6, 6);
        assertThrows(OxonoException.class, () -> {
            board.moveTotem(new Totem(Mark.X), invalidPosition);
        });
    }

    /**
     * Tests the insertion of a valid pawn on the board.
     *
     * This method verifies that a pawn of specified attributes is correctly inserted
     * into a designated position on the board when the operation is valid. It ensures
     * that the token located at the pawn's position matches the inserted pawn and
     * the insertion process does not return null.
     *
     * Preconditions:
     * - A board of specified size is initialized.
     * - A totem position is retrieved for a specific mark.
     * - A valid pawn, with associated color and mark, is created.
     * - A valid position for that pawn is determined.
     *
     * Validations:
     * - Asserts that the resulting token located at the intended position on the board
     *   is not null.
     * - Asserts that the token at the specified position matches the attributes of the
     *   inserted pawn.
     */
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

    /**
     * Tests the insertion of a pawn at an invalid position on the board.
     *
     * This method ensures that the system throws an OxonoException when
     * attempting to insert a pawn at a position outside the valid boundaries
     * of the board. The test uses an invalid position that exceeds the board's
     * size and verifies the exception is correctly raised.
     */
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

    /**
     * Tests the horizontal win condition in the game board.
     * The method sets up a specific scenario where multiple pawns are inserted sequentially in a horizontal
     * alignment for a given mark, then verifies if the win-checking mechanism correctly identifies this condition.
     *
     * The test performs the following steps:
     * 1. Initializes a game board and a totem with a particular mark.
     * 2. Inserts pawns of the specified mark at predefined positions in one row of the board.
     * 3. Moves the totem to simulate player moves.
     * 4. Invokes the win-checking method and asserts it returns true for this configuration.
     */
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


    /**
     * Tests the vertical win condition for the game board.
     *
     * The method sets up a specific scenario on the game board where a player
     * achieves a vertical winning condition for their mark. It initializes a board
     * and simulates several moves and pawn insertions by different Totems (X and O).
     *
     * The test asserts that the game recognizes the correct vertical win condition
     * after the moves and insertions have been performed.
     */
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

    /**
     * Tests the functionality of the `getMovesPossibles` method in the `Board` class.
     * Verifies that the method returns a non-empty list of possible moves for a totem located
     * in a specific position on the board. The test initializes a board, retrieves the position
     * of a totem with a specific marking, and obtains the possible moves from that position.
     * Assertions ensure the returned list is not empty.
     */
    @Test
    public void testGetMovesPossibles() {
        Board board = new Board(5);
        Position totemPosition = board.getTotemPosition(Mark.X);

        List<Position> moves = board.getMovesPossibles(totemPosition);
        assertFalse(moves.isEmpty());
    }

    /**
     * Tests the method isValidMove of the Board class to verify if moves are correctly identified as valid
     * or invalid based on the game rules.
     *
     * The test includes:
     * - Validation of a move within the board's boundaries as a valid move.
     * - Verification that out-of-bound moves throw an OxonoException.
     */
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

    /**
     * Tests the functionality of retrieving all adjacent cells of a given position on the board.
     * Ensures that the method {@link Board#getAdjacentCells(Position)} correctly identifies
     * and returns a non-empty list of valid adjacent positions surrounding the totem's current position.
     * Verifies the result is not empty when the board is initialized and a totem position is present.
     */
    @Test
    public void testGetAdjacentCells() {
        Board board = new Board(5);
        Position totemPosition = board.getTotemPosition(Mark.X);

        List<Position> adjacentCells = board.getAdjacentCells(totemPosition);
        assertFalse(adjacentCells.isEmpty());
    }

}