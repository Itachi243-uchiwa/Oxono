package dev3.projet.oxono_g63888.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a board for the Oxono game.
 * The game consists of a square board where players move totems and place pawns
 * to create winning alignments of 4 pieces of the same mark (X/O) or color.
 */
public class Board {
    private Token[][] board;

    private Position totemPosX;
    private Position totemPosO;

    private int size;

    /**
     * Creates a new board with the specified size
     * @param size The width/height of the square board
     */
    public Board(int size) {
        this.size = size;
        initializeBoard(size);
    }

    /**
     * Initializes an empty board and places the initial totems
     * X totem starts at position (2,2)
     * O totem starts at position (3,3)
     */
    private void initializeBoard(int size) {
        board = new Token[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                board[row][col] = null;
            }
        }

        int medium = size/2;
        board[medium-1][medium-1] = new Totem(Mark.X);
        board[medium][medium] = new Totem(Mark.O);
        totemPosX = new Position(medium -1, medium-1);
        totemPosO = new Position(medium, medium);
    }

    /**
     * Gets the current position of a totem based on its mark (X or O)
     */
    public Position getTotemPosition(Mark mark) {
        return mark == Mark.X ? totemPosX : totemPosO;
    }

    /**
     * Returns a list of all empty positions on the board
     */
    public List<Position> allPositionsEmpty() {
        List<Position> emptyPositions = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (isEmpty(new Position(row, col))) {
                    emptyPositions.add(new Position(row, col));
                }
            }
        }
        return emptyPositions;
    }

    /**
     * Checks if a position is empty (contains no token)
     */
    private boolean isEmpty(Position pos) {
        if (!isInBounds(pos)) {
            throw new OxonoException("Position hors des limites du plateau: " + pos);
        }

        return board[pos.row()][pos.column()] == null;
    }


    /**
     * Checks if a position is within the board boundaries
     */
    private boolean isInBounds(Position pos) {
        return pos.row() >= 0 && pos.row() < size && pos.column() >= 0 && pos.column() < size;
    }

    /**
     * Moves a totem to a new position if the move is valid
     * Updates the totem's position tracker accordingly
     */
    public void moveTotem(Token totem, Position newpos) {
        Position oldTotemPos = getTotemPosition(totem.getMark());
        if (isValidMove(newpos, oldTotemPos)) {
            insertToken(totem, newpos);
            board[oldTotemPos.row()][oldTotemPos.column()] = null;
            if (totem.getMark() == Mark.X) {
                totemPosX = newpos;
            } else {
                totemPosO = newpos;
            }
        }
    }

    /**
     * Inserts a pawn at the specified position if it's a valid insertion
     * Pawns can only be placed in valid positions relative to their totem
     */
    public void insertPawn(Pawn pawn, Position pos, Position totemPos) {
        if (isValidInsertion(pos, totemPos)) {
            insertToken(pawn, pos);
        }
    }

    public void removePawn(Position pawnPosition) {
        if (!isEmpty(pawnPosition)) {
            board[pawnPosition.row()][pawnPosition.column()] = null;
        }
    }

    /**
     * Inserts a token at the specified position if it's empty and within bounds
     * @throws OxonoException if the position is invalid
     */
    private void insertToken(Token token, Position pos) {
        if (isEmpty(pos) && isInBounds(pos)) {
            board[pos.row()][pos.column()] = token;
        } else {
            throw new OxonoException("Invalid position");
        }
    }

    /**
     * Validates if a totem can move to the new position from its current position
     */
    public boolean isValidMove(Position newpos, Position oldpos) {
        if (!(isEmpty(newpos) || !isInBounds(newpos))) {
            System.out.println(!isEmpty(newpos));
            System.out.println(isInBounds(newpos));
            return false;
        }
        return getMovesPossibles(oldpos).contains(newpos);
    }

    /**
     * Gets all possible moves for a token at the given position
     * If the totem is landlocked (surrounded), it can move anywhere if the next position is empty
     * Otherwise, it can move along its row and column until blocked
     */
    public List<Position> getMovesPossibles(Position pos) {
        if (isLandLockedTotem(pos)) {
            if (nextPositionEmpty(pos).isEmpty()) {
                return allPositionsEmpty();
            }
            return nextPositionEmpty(pos);
        }
        return allPositionsEmptyInRowandCol(pos);
    }

    /**
     * Checks if a totem is surrounded by other pieces (landlocked)
     */
    private boolean isLandLockedTotem(Position pos) {
        for (Direction dir : Direction.values()) {
            int newRow = pos.row() + dir.getDeltaX();
            int newCol = pos.column() + dir.getDeltaY();

            if (isInBounds(new Position(newRow, newCol))) {
                if (isEmpty(new Position(newRow, newCol))) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Finds the next empty position in each direction from the given position
     */
    private List<Position> nextPositionEmpty(Position pos) {
        List<Position> nextPositions = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            int currentRow = pos.row() + dir.getDeltaX();
            int currentCol = pos.column() + dir.getDeltaY();
            Position currentPos = new Position(currentRow, currentCol);

            while (isInBounds(currentPos)) {
                if (isEmpty(currentPos)) {
                    nextPositions.add(currentPos);
                    break;
                }
                currentRow += dir.getDeltaX();
                currentCol += dir.getDeltaY();
            }
        }
        return nextPositions;
    }

    /**
     * Gets all empty positions in the same row and column as the given position
     * Stops checking in a direction when it hits a non-empty position
     */
    private List<Position> allPositionsEmptyInRowandCol(Position pos) {
        List<Position> positions = new ArrayList<Position>();
        for (Direction dir : Direction.values()) {
            int currentRow = pos.row() + dir.getDeltaX();
            int currentCol = pos.column() + dir.getDeltaY();

            while (isInBounds(new Position(currentRow, currentCol))) {
                if (isEmpty(new Position(currentRow, currentCol))) {
                    positions.add(new Position(currentRow, currentCol));
                } else {
                    break;
                }
                currentRow += dir.getDeltaX();
                currentCol += dir.getDeltaY();
            }
        }
        return positions;
    }

    /**
     * Validates if a pawn can be inserted at the given position relative to its totem
     */
    public boolean isValidInsertion(Position pawnPos, Position totemPos){
        if (!(isEmpty(pawnPos) || !isInBounds(pawnPos))){
            return false;
        }
        return getInsertionPositions(totemPos).contains(pawnPos);
    }

    /**
     * Gets valid insertion positions for pawns relative to their totem
     * If the totem has no adjacent cells, pawns can be placed anywhere
     */
    public List<Position> getInsertionPositions(Position totempos) {
        return getAdjacentCells(totempos).isEmpty() ? allPositionsEmpty() : getAdjacentCells(totempos);
    }

    /**
     * Gets the token at a specific position
     */
    public Token getPositionToken(Position pos) {
        return board[pos.row()][pos.column()];
    }

    /**
     * Gets all adjacent cells to a totem that are either empty or out of bounds
     */
    public List<Position> getAdjacentCells(Position totempos) {
        List<Position> adjacentCells = new ArrayList<>();
        if (isTotem(getPositionToken(totempos))) {

            for (Direction direction : Direction.values()) {
                int newX = totempos.row() + direction.getDeltaX();
                int newY = totempos.column() + direction.getDeltaY();
                Position pos = new Position(newX, newY);

                if (isInBounds(pos)) {
                    if (isEmpty(pos)) {
                        adjacentCells.add(pos);
                    }
                }
            }
        }

        return adjacentCells;
    }


    /**
     * Checks if a token is a totem
     */
    public boolean isTotem(Token token) {
        return token instanceof Totem;
    }


    /**
     * Checks if there's a winning condition for a pawn at the given position
     * Wins can be achieved by aligning 4 pawns of the same mark or color
     */
    public boolean checkWin(Pawn pawn, Position pos) {
        return checkLineWin(pawn, pos.row()) || checkColumnWin(pawn, pos.column());
    }

    /**
     * Checks if there are 4 pawns aligned horizontally with the same mark or color
     */
    private boolean checkLineWin(Pawn pawn, int row) {
        int markCount = 0;
        int colorCount = 0;

        for (int i = 0; i < size; i++) {
            Token currentToken = board[row][i];

            if (!isValidPawn(currentToken)) {
                markCount = 0;
                colorCount = 0;
                continue;
            }

            int[] updatedCounts = updateCounters(currentToken, pawn, markCount, colorCount);
            markCount = updatedCounts[0];
            colorCount = updatedCounts[1];

            if (hasWinningSequence(markCount, colorCount)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumnWin(Pawn pawn, int col) {
        int markCount = 0;
        int colorCount = 0;

        for (int i = 0; i < size; i++) {
            Token currentToken = board[i][col];

            if (!isValidPawn(currentToken)) {
                markCount = 0;
                colorCount = 0;
                continue;
            }

            int[] updatedCounts = updateCounters(currentToken, pawn, markCount, colorCount);
            markCount = updatedCounts[0];
            colorCount = updatedCounts[1];

            if (hasWinningSequence(markCount, colorCount)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidPawn(Token token) {
        return token != null && !(isTotem(token));
    }
    private int[] updateCounters(Token currentToken, Pawn referencePawn, int markCount, int colorCount) {
        if (!(currentToken instanceof Pawn currentPawn)) {
            return new int[]{markCount, colorCount};
        }
        markCount = (currentPawn.getMark() == referencePawn.getMark()) ? markCount + 1 : 0;

        colorCount = (currentPawn.getColor() == referencePawn.getColor()) ? colorCount + 1 : 0;

        return new int[]{markCount, colorCount};
    }

    public Token getToken(Position position){
        return board[position.row()][position.column()];
    }
    public Totem getTotem(Position position){
        return (Totem) board[position.row()][position.column()];
    }

    /**
     * Checks if either counter has reached the winning sequence length (4)
     */
    private boolean hasWinningSequence(int markCount, int colorCount) {
        final int WINNING_SEQUENCE = 4;
        return markCount == WINNING_SEQUENCE || colorCount == WINNING_SEQUENCE;
    }
    public int getSize(){
        return size;
    }
}