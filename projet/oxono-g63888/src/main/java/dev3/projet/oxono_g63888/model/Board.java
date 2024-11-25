package dev3.projet.oxono_g63888.model;

import dev3.projet.oxono_g63888.model.strategy.ZobristHashing;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Token[][] board;

    private Position totemPosX;
    private Position totemPosO;

    private int size;
    private ZobristHashing zobristHashing;
    private long currentHash;
    private List<Position> winningPositions;

    public Board(int size) {
        this.size = size;
        this.winningPositions = new ArrayList<>();
        this.zobristHashing = new ZobristHashing(size);
        this.currentHash = 0L;
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

        int medium = size / 2;
        board[medium - 1][medium - 1] = new Totem(Mark.X);
        board[medium][medium] = new Totem(Mark.O);
        totemPosX = new Position(medium - 1, medium - 1);
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
    public boolean isEmpty(Position pos) {
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
            currentHash ^= zobristHashing.getHashForTotem(newpos.row(), newpos.column(), totem.getMark());

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

            currentHash ^= zobristHashing.getHashForPawn(pos.row(), pos.column(), pawn.getColor());

            insertToken(pawn, pos);
        }
    }


    /**
    * Removes a pawn from the board at the specified position.
    * @param pawnPosition The position of the pawn to be removed.
    * @throws OxonoException If the position is invalid (out of bounds or does not contain a pawn).
    */
    public void removePawn(Position pawnPosition) {

        if (isEmpty(pawnPosition)){
            throw new OxonoException("Invalid position: no pawn found at " + pawnPosition);
        }
        Pawn pawn = (Pawn) getToken(pawnPosition);
        currentHash ^= zobristHashing.getHashForPawn(pawnPosition.row(), pawnPosition.column(), pawn.getColor());
        board[pawnPosition.row()][pawnPosition.column()] = null;
    }




    /**
     * Inserts a token at the specified position if it's empty and within bounds
     *
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
        return getAdjacentCells(pos).isEmpty();
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
                currentPos = new Position(currentRow, currentCol);
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
            Position currentPos = new Position(currentRow, currentCol);

            while (isInBounds(currentPos)) {
                if (isEmpty(currentPos)) {
                    positions.add(currentPos);
                } else {
                    break;
                }
                currentRow += dir.getDeltaX();
                currentCol += dir.getDeltaY();
                currentPos = new Position(currentRow, currentCol);
            }
        }
        return positions;
    }

    /**
     * Validates if a pawn can be inserted at the given position relative to its totem
     */
    public boolean isValidInsertion(Position pawnPos, Position totemPos) {
        if (!(isEmpty(pawnPos) || !isInBounds(pawnPos))) {
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
     * Checks if the given pawn has won the game by forming a sequence of 4 or more consecutive pawns
     * in a horizontal or vertical direction.
     *
     * @param pawn  The pawn to check for a win
     * @param pos    The position of the pawn on the board
     * @return true if the pawn has won, false otherwise
     */
    public boolean checkWin(Pawn pawn, Position pos) {
        // (LEFT + RIGHT)
        List<Position> horizontalPositions = checkDirectionalWin(pawn, pos, Direction.LEFT, Direction.RIGHT);
        if (horizontalPositions.size() >= 4) {
            winningPositions = horizontalPositions;
            return true;
        }

        // (UP + DOWN)
        List<Position> verticalPositions = checkDirectionalWin(pawn, pos, Direction.UP, Direction.DOWN);
        if (verticalPositions.size() >= 4) {
            winningPositions = verticalPositions;
            return true;
        }

        winningPositions = new ArrayList<>();
        return false;
    }
    /**
     * Checks if the given pawn has won the game by forming a sequence of 4 or more consecutive pawns
     * in a horizontal or vertical direction.
     * @param pawn  The pawn to check for a win
     * @param pos    The position of the pawn on the board
     * @return true if the pawn has won, false otherwise
     */
    private List<Position> checkDirectionalWin(Pawn pawn, Position pos, Direction dir1, Direction dir2) {
        List<Position> colorMatches = checkSequence(pawn, pos, dir1, dir2, true);
        if (colorMatches.size() >= 4) {
            return colorMatches;
        }
        return checkSequence(pawn, pos, dir1, dir2, false);
    }


        /**
     * Checks a sequence of pawns in a specific direction and returns a list of matching positions.
     *
     * @param pawn       The pawn to check for a sequence.
     * @param pos        The position of the pawn on the board.
     * @param dir1       The first direction to check for a sequence.
     * @param dir2       The second direction to check for a sequence.
     * @param checkColor Whether to check for matching colors or marks.
     * @return A list of positions that form a sequence of 4 or more consecutive pawns.
     */
    private List<Position> checkSequence(Pawn pawn, Position pos, Direction dir1, Direction dir2, boolean checkColor) {
        List<Position> positions = new ArrayList<>();
        positions.add(pos);

        positions.addAll(checkDirection(pawn, pos, dir1, checkColor));

        positions.addAll(checkDirection(pawn, pos, dir2, checkColor));

        positions.sort((p1, p2) -> {
            int rowCompare = Integer.compare(p1.row(), p2.row());
            return rowCompare != 0 ? rowCompare : Integer.compare(p1.column(), p2.column());
        });

        return positions;
    }

        /**
     * Checks a sequence of pawns in a specific direction and returns a list of matching positions.
     *
     * @param pawn       The pawn to check for a sequence.
     * @param pos        The position of the pawn on the board.
     * @param dir1       The first direction to check for a sequence.
     * @param dir2       The second direction to check for a sequence.
     * @param checkColor Whether to check for matching colors or marks.
     * @return A list of positions that form a sequence of 4 or more consecutive pawns.
     */
    private List<Position> checkDirection(Pawn pawn, Position pos, Direction dir, boolean checkColor) {
        List<Position> positions = new ArrayList<>();
        int currentRow = pos.row() + dir.getDeltaX();
        int currentCol = pos.column() + dir.getDeltaY();
        Position currentPosition = new Position(currentRow, currentCol);

        while (isInBounds(currentPosition)) {
            Token token = board[currentRow][currentCol];
            if (!isValidPawn(token)) break;

            Pawn currentPawn = (Pawn) token;
            if (!isPawnMatch(currentPawn, pawn, checkColor)) break;

            positions.add(new Position(currentRow, currentCol));
            currentRow += dir.getDeltaX();
            currentCol += dir.getDeltaY();
            currentPosition = new Position(currentRow, currentCol);
        }

        return positions;
    }

    private boolean isValidPawn(Token token) {
        return token != null && !isTotem(token);
    }
    private boolean isPawnMatch(Pawn currentPawn, Pawn referencePawn, boolean checkColor) {
        return checkColor ?
                currentPawn.getColor() == referencePawn.getColor() :
                currentPawn.getMark() == referencePawn.getMark();
    }
    public List<Position> getWinnigPositions() {
        return winningPositions;
    }
    public Token getToken(Position position) {
        return board[position.row()][position.column()];
    }

    public Totem getTotem(Position position) {
        return (Totem) board[position.row()][position.column()];
    }

    public int getSize() {
        return size;
    }
    public long calculateZobristHash() { return currentHash; }

    public Board copy() {
        Board copiedBoard = new Board(this.size);
        copiedBoard.currentHash = this.currentHash;
        copiedBoard.totemPosX = this.totemPosX; // Immutable Position, peut être partagé
        copiedBoard.totemPosO = this.totemPosO; // Immutable Position, peut être partagé

        // Copier les positions gagnantes
        copiedBoard.winningPositions = new ArrayList<>(this.winningPositions);

        // Copier le tableau des Tokens
        copiedBoard.board = new Token[this.size][this.size];
        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < this.size; col++) {
                if (this.board[row][col] != null) {
                    copiedBoard.board[row][col] = this.board[row][col].copy(); // Supposons que Token a une méthode copy()
                }
            }
        }

        // Copier l'objet de hachage Zobrist
        copiedBoard.zobristHashing = this.zobristHashing; // Si ZobristHashing est immuable, peut être partagé

        return copiedBoard;
    }


}