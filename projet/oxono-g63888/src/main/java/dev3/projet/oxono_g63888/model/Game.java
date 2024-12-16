package dev3.projet.oxono_g63888.model;

import dev3.projet.oxono_g63888.model.Observer.*;
import dev3.projet.oxono_g63888.model.commands.*;
import dev3.projet.oxono_g63888.model.strategy.*;

import java.util.ArrayList;
import java.util.List;

public class Game implements Observable {
    private static Board board;
    private Player[] players;
    private Player currentPlayer;
    private AIPlayer aiPlayer;
    private Player humanPlayer;
    private Position totemXposition;
    private Position totemOposition;
    private Totem lastTotemPlay;
    private CommandManager invoker;
    private Position lastPawnPosition;
    private List<Observer> observers;
    private boolean isUndoRedoInProgress;
    private GameState state;
    private static int sizeBoard;

    public Game() {
        this.observers = new ArrayList<>();
        this.isUndoRedoInProgress = false;
        this.state = GameState.STARTED;
    }

    /**
     * Initializes the game with the specified board size and game mode.
     * Sets up the game components such as the board, players, and game state,
     * and notifies observers that the game has started.
     *
     * @param sizeBoard the size of the board to be created for the game
     * @param gameMode the game mode to determine the type of players:
     *                 1 for single-player with another human,
     *                 2 for single-player against a Random AI,
     *                 3 for single-player against an Advanced AI,
     *                 4 for AI vs AI mode
     */
    public void initializeGame(int sizeBoard, int gameMode) {
        this.sizeBoard = sizeBoard;
        this.board = new Board(sizeBoard);
        humanPlayer = new Player(ColorPawn.PINK);

        switch (gameMode) {
            case 2 -> {
                aiPlayer = new AIPlayer(ColorPawn.BLACK, new RandomAIStrategy(ColorPawn.BLACK));
                players = new Player[]{humanPlayer, aiPlayer};
            }
            case 3 -> {
                aiPlayer = new AIPlayer(ColorPawn.BLACK, new AdvancedAIStrategy(ColorPawn.BLACK));
                players = new Player[]{humanPlayer, aiPlayer};
            }
            case 4 -> {
                Player aiPlayer1 = new AIPlayer(ColorPawn.PINK, new RandomAIStrategy(ColorPawn.PINK));
                Player aiPlayer2 = new AIPlayer(ColorPawn.BLACK, new RandomAIStrategy(ColorPawn.BLACK));
                players = new Player[]{aiPlayer1, aiPlayer2};
            }
            default -> players = new Player[]{humanPlayer, gameMode == 1 ? new Player(ColorPawn.BLACK) : aiPlayer};
        }

        this.totemXposition = board.getTotemPosition(Mark.X);
        this.totemOposition = board.getTotemPosition(Mark.O);
        currentPlayer = players[0];
        this.lastTotemPlay = null;
        this.lastPawnPosition = new Position(0, 0);
        this.invoker = new CommandManager();

        notifyObservers(new OxonoEvent(ObservableEvent.GAME_START)
                .addData("boardSize", board.getSize())
                .addData("players", players)
                .addData("currentPlayer", currentPlayer)
        );
        this.state = GameState.WAITING_FOR_TOTEM;
    }

    /**
     * Plays the AI's turn in the game.
     *
     * @throws OxonoException if the current player is not an AI player.
     */
    public void playAITurn() throws OxonoException {
        if (!isCurrentPlayerAI()) {
            throw new OxonoException("Current player is not AI");
        }
        if (isUndoRedoInProgress) {
            return;
        }

        aiPlayer = (AIPlayer) currentPlayer;

        try {
            executeTotemMove(aiPlayer.getNextTotemMove(board));
            executePawnMove(aiPlayer.getNextMove(board));
            state = GameState.WAITING_FOR_TOTEM;
        } catch (OxonoException e) {
            throw new OxonoException(e.getMessage());
        }

    }

    /**
     * Executes the AI's move for the specified totem.
     *
     * @param totemMove The move to be executed, containing the token and the new position for the totem.
     * @throws OxonoException If the current player is not an AI player or if the totem move is invalid.
     */
    private void executeTotemMove(Move totemMove) throws OxonoException {
        Totem totemAI = (Totem) totemMove.token();
        Position newPosTotem = totemMove.movePosition();
        Position totemPos = getTotemPositionForMove(totemAI.getMark());

        if (!board.isValidMove(newPosTotem, totemPos)) {
            throw new OxonoException("Invalid totem move");
        }
        board.moveTotem(totemAI, newPosTotem);
        Command command = new MoveTotemCommand(totemAI, newPosTotem, totemPos);
        invoker.executeCommand(command);
        lastTotemPlay = totemAI;
        updateTotemPosition(lastTotemPlay, newPosTotem);

        notifyObservers(new OxonoEvent(ObservableEvent.MOVE_TOTEM)
                .addData("totem", lastTotemPlay)
                .addData("oldPosition", totemPos)
                .addData("newPosition", newPosTotem)
        );

    }

    /**
     * Executes the pawn move based on the given move.
     *
     * @param pawnMove The move to be executed, containing the token and the new position for the pawn.
     * @throws OxonoException If the last moved totem's mark does not match the pawn's mark or if the pawn move is invalid.
     */
    private void executePawnMove(Move pawnMove) throws OxonoException {
        Token token = pawnMove.token();
        Position pawnPos = pawnMove.movePosition();
        Position totemPos = getTotemPositionForMove(token.getMark());

        if (lastTotemPlay == null || lastTotemPlay.getMark() != token.getMark()) {
            throw new OxonoException("Invalid mark, the pawn's mark must match the last moved totem");
        }

        if (!board.isValidInsertion(pawnPos, totemPos)) {
            throw new OxonoException("Invalid pawn move");
        }
        board.insertPawn((Pawn) token, pawnPos, totemPos);
        Command command = new InsertPawnCommand((Pawn) token, pawnPos);
        invoker.executeCommand(command);
        lastPawnPosition = pawnPos;
        currentPlayer.usePawn(token.getMark());
        notifyObservers(new OxonoEvent(ObservableEvent.PLACE_PAWN)
                .addData("pawn", token)
                .addData("newPosition", pawnPos)
                .addData("oldPosition", lastPawnPosition)
        );
        if (!(checkWinCondition() || isDraw())) {
            switchPlayer();
        }
    }

    private Position getTotemPositionForMove(Mark mark) {
        return mark == Mark.X ? totemXposition : totemOposition;
    }

    private void updateTotemPosition(Totem totem, Position newPos) {
        if (totem.getMark() == Mark.X) {
            totemXposition = newPos;
        } else {
            totemOposition = newPos;
        }
    }

    /**
     * Processes the input for a totem move in the game.
     * This method validates and executes the totem move based on the provided input.
     * After processing the totem move, it updates the game state to wait for a pawn move.
     *
     * @param totemInput A string representing the totem move input.
     *                   Expected format: "totem [row] [column]"
     * @throws OxonoException If the input is invalid or the move is not allowed
     */
    public void processTotemInput(String totemInput) throws OxonoException {
        processInput(totemInput, true);
        state = GameState.WAITING_FOR_PAWN;
    }

    /**
     * Processes the input for a pawn move in the game.
     * This method validates and executes the pawn move based on the provided input.
     * After processing the pawn move, it updates the game state to wait for a totem move.
     *
     * @param pawnInput A string representing the pawn move input.
     *                  Expected format: "pawn [row] [column]"
     * @throws OxonoException If the input is invalid or the move is not allowed
     */
    public void processPawnInput(String pawnInput) throws OxonoException {
        processInput(pawnInput, false);
        state = GameState.WAITING_FOR_TOTEM;
    }

    /**
     * Processes the input for either a totem or pawn move in the game.
     * This method parses the input, validates it, and executes the appropriate move.
     * It also notifies observers about the move that was made.
     *
     * @param input   A string representing the move input.
     *                Expected format: "totem [row] [column]" or "pawn [row] [column]"
     * @param isTotem A boolean flag indicating whether the move is for a totem (true) or a pawn (false)
     * @throws OxonoException If the input format is invalid or if the move is not allowed
     */
    private void processInput(String input, boolean isTotem) throws OxonoException {
        String[] parts = input.split(" ");
        if (parts.length != 3) {
            throw new OxonoException("Invalid input format. Use 'totem [row] [column]' or 'pawn [row] [column]'");
        }
        Mark mark = Mark.valueOf(isTotem ? parts[0] : parts[0].substring(1));
        Position newPosition = new Position(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        Position actualTotemPos = getTotemPositionForMove(mark);

        if (!currentPlayer.hasPawn(mark)) {
            throw new OxonoException("Player does not have a pawn of the specified mark");
        }
        try {
            Command command = null;

            if (isTotem) {
                processTotemMove(newPosition, actualTotemPos);
                command = new MoveTotemCommand(board.getTotem(newPosition), newPosition, actualTotemPos);
                notifyObservers(new OxonoEvent(ObservableEvent.MOVE_TOTEM)
                        .addData("totem", lastTotemPlay)
                        .addData("oldPosition", actualTotemPos)
                        .addData("newPosition", newPosition));
            } else {
                processPawnMove(mark, newPosition, actualTotemPos);
                Pawn pawn = new Pawn(currentPlayer.getColor(), mark);
                command = new InsertPawnCommand(pawn, newPosition);
                currentPlayer.usePawn(mark);
                notifyObservers(new OxonoEvent(ObservableEvent.PLACE_PAWN)
                        .addData("pawn", pawn)
                        .addData("oldPosition", lastPawnPosition)
                        .addData("newPosition", newPosition));

                if (!(checkWinCondition() || isDraw())) {
                    switchPlayer();
                }
            }
            invoker.executeCommand(command);

        } catch (IllegalArgumentException e) {
            throw new OxonoException("Invalid input data");
        }
    }

    private void processTotemMove(Position newPosition, Position actualTotemPos) throws OxonoException {
        Totem totem = board.getTotem(actualTotemPos);

        if (!board.isValidMove(newPosition, actualTotemPos)) {
            throw new OxonoException("Invalid totem move");
        }
        board.moveTotem(totem, newPosition);
        lastTotemPlay = totem;
        updateTotemPosition(totem, newPosition);

    }

    private void processPawnMove(Mark mark, Position newPosition, Position actualTotemPos) throws OxonoException {
        if (lastTotemPlay.getMark() != mark) {
            throw new OxonoException("Invalid mark, choose the pawn whose mark is equal to the totem moved");
        }

        if (!board.isValidInsertion(newPosition, actualTotemPos)) {
            throw new OxonoException("Invalid Pawn move");
        }
        Pawn pawn = new Pawn(currentPlayer.getColor(), mark);
        board.insertPawn(pawn, newPosition, actualTotemPos);
        lastPawnPosition = newPosition;

    }


    /**
     * Checks if the current game state satisfies the win condition.
     * This method evaluates the last pawn placement to determine if it results in a win.
     * If a win is detected, it updates the game state and notifies observers.
     *
     * @return true if the current state is a winning condition, false otherwise.
     */
    public boolean checkWinCondition() {
        Token token = board.getToken(lastPawnPosition);
        boolean isWinning = board.checkWin((Pawn) token, lastPawnPosition);
        if (isWinning) {
            state = GameState.GAME_OVER;
            notifyObservers(new OxonoEvent(ObservableEvent.WIN)
                    .addData("winner", currentPlayer)
                    .addData("winningPosition", board.getWinnigPositions())
            );
        }

        return isWinning;
    }

    /**
     * Switches the current player to the other player in the game.
     * This method alternates the `currentPlayer` field between the two
     * players stored in the `players` array. If the `currentPlayer`
     * is the first player in the array, it switches to the second
     * player, and vice versa.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
    }

    /**
     * Checks whether the game has ended in a draw.
     * A draw occurs if the game is not in the GAME_OVER state and both players have no pawns remaining.
     * If a draw is detected, it notifies observers with a DRAW event and updates the game state to GAME_OVER.
     *
     * @return true if the game ends in a draw, false otherwise.
     */
    public boolean isDraw() {
        boolean isDrawing = false;
        if (state != GameState.GAME_OVER) {
            isDrawing = players[0].dontHaveAnyPawns() && players[1].dontHaveAnyPawns();
            if (isDrawing) {
                notifyObservers(new OxonoEvent(ObservableEvent.DRAW));
                state = GameState.GAME_OVER;
            }
        }
        return isDrawing;
    }


    /**
     * Undoes the last action performed in the game, reverting the game state
     * to its previous condition. Depending on the nature of the action undone,
     * it adjusts the game state and notifies the observers of the change.
     * The undo operation can revert either a pawn placement or a totem movement.
     *
     * Before undoing, the method checks if the game permits undoing in the
     * current state and if the previous action can indeed be undone. Specific
     * game states, such as GAME_OVER, prohibit undo operations.
     *
     * @return true if the undo operation was successfully performed;
     *         false if undoing was not allowed or not possible.
     */
    public boolean undo() {
        if (state == GameState.GAME_OVER || !canUndo()) {
            return false;
        }

        Command command = invoker.undo();

        isUndoRedoInProgress = true;
        try {
            if (command instanceof InsertPawnCommand pawnCommand) {
                lastPawnPosition = pawnCommand.getPawnPosition();
                Pawn pawn = pawnCommand.getPawn();
                switchPlayer();

                if (invoker.getUndoPeek() instanceof MoveTotemCommand totemCommand) {
                    lastTotemPlay = totemCommand.getTotem();
                }

                state = GameState.WAITING_FOR_PAWN;
                board.removePawn(lastPawnPosition);
                currentPlayer.returnPawn(pawn.getMark());
                notifyObservers(new OxonoEvent(ObservableEvent.UNDO)
                        .addData("pawn", pawn)
                        .addData("currentPlayer", currentPlayer)
                        .addData("newPosition", pawnCommand.getPawnPosition()));
            } else if (command instanceof MoveTotemCommand totemCommand) {
                Position oldPosition = totemCommand.getOldPosition();
                Totem totem = totemCommand.getTotem();
                updateTotemPosition(totem, oldPosition);
                board.moveTotem(totem, oldPosition);
                lastTotemPlay = null;

                state = GameState.WAITING_FOR_TOTEM;

                notifyObservers(new OxonoEvent(ObservableEvent.UNDO)
                        .addData("oldPosition", oldPosition)
                        .addData("totem", totem)
                        .addData("newPosition", totemCommand.getNewPosition()));
            }
        } finally {
            isUndoRedoInProgress = false;
        }
        return true;
    }


    /**
     * Redoes the most recently undone game action, restoring the game state to what it was
     * after the redone move was executed. This method manages the reapplication of either
     * a totem or pawn move, including updating positions, game state, and notifying observers.
     * The operation will not succeed if the game is over or if no redo action is available.
     *
     * @return true if the redo operation was successfully completed, false otherwise.
     * @throws OxonoException if the redo operation cannot be performed due to an internal issue.
     */
    public boolean redo() throws OxonoException {
        if (state == GameState.GAME_OVER || !canRedo()) {
            return false;
        }

        Command command = invoker.redo();

        isUndoRedoInProgress = true;
        try {
            if (command instanceof MoveTotemCommand totemCommand) {
                lastTotemPlay = totemCommand.getTotem();
                Position newPosition = totemCommand.getNewPosition();
                updateTotemPosition(lastTotemPlay, newPosition);
                board.moveTotem(lastTotemPlay, newPosition);
                state = GameState.WAITING_FOR_PAWN;

                notifyObservers(new OxonoEvent(ObservableEvent.REDO)
                        .addData("oldPosition", totemCommand.getOldPosition())
                        .addData("totem", lastTotemPlay)
                        .addData("newPosition", newPosition));
            } else if (command instanceof InsertPawnCommand pawnCommand) {
                Pawn pawn = pawnCommand.getPawn();
                lastPawnPosition = pawnCommand.getPawnPosition();
                board.insertPawn(pawn, lastPawnPosition, getTotemPositionForMove(lastTotemPlay.getMark()));
                currentPlayer.usePawn(pawn.getMark());
                switchPlayer();

                state = GameState.WAITING_FOR_TOTEM;

                notifyObservers(new OxonoEvent(ObservableEvent.REDO)
                        .addData("pawn", pawnCommand.getPawn())
                        .addData("newPosition", pawnCommand.getPawnPosition())
                        .addData("currentPlayer", currentPlayer));
            }
        } finally {
            isUndoRedoInProgress = false;
        }
        return true;
    }

    /**
     * Retrieves a list of possible moves for the totem located at the specified position.
     * The calculation of possible moves depends on the current state of the board
     * and the rules associated with totem movement.
     *
     * @param position the position of the totem for which possible moves are to be determined.
     * @return a list of possible positions to which the totem can move.
     */
    public List<Position> getMovesPossilesForTotem(Position position) {
        return board.getMovesPossibles(position);
    }

    /**
     * Determines whether an undo operation can be performed in the current game state.
     * The ability to undo depends on whether the invoker has actions that can be undone
     * and whether an undo or redo operation is not already in progress.
     *
     * @return true if an undo operation can be performed, false otherwise.
     */
    public boolean canUndo() {
        return invoker.canUndo() && !isUndoRedoInProgress;
    }

    /**
     * Checks if a redo operation can be performed in the current state of the game.
     * A redo operation is only available if the invoker stack allows it and no undo/redo operation
     * is currently in progress.
     *
     * @return true if a redo operation is possible, false otherwise.
     */
    public boolean canRedo() {
        return invoker.canRedo() && !isUndoRedoInProgress;
    }

    /**
     * Checks whether an undo or redo operation is currently in progress.
     *
     * @return true if an undo or redo operation is being performed, false otherwise.
     */
    public boolean isUndoRedoInProgress() {
        return isUndoRedoInProgress;
    }

    /**
     * Determines whether the current player in the game is an AI player.
     *
     * @return true if the current player is an instance of AIPlayer, false otherwise.
     */
    public boolean isCurrentPlayerAI() {
        return currentPlayer instanceof AIPlayer;
    }

    /**
     * Returns a string representation of the current player's state.
     *
     * @return A string containing information about the current player.
     */
    public String getToString() {
        return currentPlayer.toString();
    }

    /**
     * Handles the surrender action for the current player in the game.
     * When a player surrenders, the game announces the winner and updates the game state accordingly.
     * The observers are notified of the surrender event along with the winning player's details.
     *
     * Behavior:
     * - Logs a message indicating the player who surrendered and the winner (AI or Human).
     * - Notifies observers with an {@link OxonoEvent} containing:
     *   - Event type: ObservableEvent.SURRENDER
     *   - Additional data: the winning player details under the key "PlayerWin".
     * - Updates the game state to GameState.SURRENDER.
     */
    public void surrender() {
        System.out.println("Player " + currentPlayer.getColor() + " surrendered! " +
                (currentPlayer == players[0] ? "AI wins!" : "Human wins!"));

        notifyObservers(new OxonoEvent(ObservableEvent.SURRENDER)
                .addData("PlayerWin", getOpponent()));
        state = GameState.SURRENDER;
    }

    /**
     * Retrieves a list of possible positions where a pawn can be inserted,
     * relative to the provided totem position.
     *
     * @param pos The current position of the totem for which valid pawn insertion positions are calculated.
     * @return A list of {@code Position} objects representing valid pawn insertion locations based on the totem's position.
     */
    public List<Position> positionsInsert(Position pos) {
        return board.getInsertionPositions(pos);
    }

    /**
     * Registers an observer to the game.
     * The observer will be notified of events or updates in the game.
     *
     * @param o the observer to be registered
     */
    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    /**
     * Removes an observer from the list of observers.
     *
     * @param o the observer to be removed
     */
    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    /**
     * Notifies all registered observers about a specific event.
     * This method iterates through the list of observers and invokes their
     * {@code update} method, passing the current game instance and the event.
     *
     * @param event the event to be passed to the observers. Represents updates
     *              or changes in the state of the game.
     */
    @Override
    public void notifyObservers(OxonoEvent event) {
        for (Observer observer : observers) {
            observer.update(this, event);
        }
    }

    /**
     * Retrieves the number of remaining pawns for each player and each mark type.
     * This includes the counts for Mark.X and Mark.O for both players in the game.
     *
     * @return An array of integers representing the remaining pawns:
     *         [pawnsXPlayer1, pawnsOPlayer1, pawnsXPlayer2, pawnsOPlayer2].
     */
    public int[] getRemainingPawns() {
        int pawnsXplayerPink = players[0].getRemainingPawns(Mark.X);
        int pawns0playerPink = players[0].getRemainingPawns(Mark.O);
        int pawnsXplayerBlack = players[1].getRemainingPawns(Mark.X);
        int pawns0playerBlack = players[1].getRemainingPawns(Mark.O);
        return new int[]{pawnsXplayerPink, pawns0playerPink, pawnsXplayerBlack, pawns0playerBlack};
    }

    /**
     * Retrieves the current state of the game.
     *
     * @return the current {@link GameState} indicating the status of the game,
     *         such as STARTED, WAITING_FOR_TOTEM, WAITING_FOR_PAWN, GAME_OVER, AI_TURN, or SURRENDER.
     */
    public GameState getGameState() {
        return state;
    }

    /**
     * Retrieves the size of the board.
     *
     * @return the size of the board represented as an integer.
     */
    public static int size() {
        return sizeBoard;
    }

    /**
     * Retrieves the color associated with the current player.
     *
     * @return the color of the current player, represented as a {@link ColorPawn}.
     */
    public ColorPawn getColorPlayer() {
        return currentPlayer.getColor();
    }

    /**
     * Retrieves the token located at the specified position in the game.
     *
     * @param pos The position on the board from which the token is to be retrieved.
     * @return The token located at the specified position, or null if the position is empty.
     */
    public Token getToken(Position pos) {
        return board.getToken(pos);
    }

    /**
     * Retrieves the mark associated with the totem located at the specified position on the board.
     *
     * @param pos The position on the board where the totem is located.
     * @return The {@link Mark} of the totem at the specified position.
     *         Returns null if no totem exists at the given position or if the totem has no mark.
     */
    public Mark getMarkTotem(Position pos) {
        return board.getTotem(pos).getMark();
    }

    /**
     * Retrieves the player who is not the current player,
     * effectively returning the opponent in the game.
     *
     * @return The opponent player of the current player.
     */
    private Player getOpponent() {
        return currentPlayer == players[0] ? players[1] : players[0];
    }
}
