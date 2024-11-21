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
    private Totem[] totems;
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
     * Initializes a new game with the specified board size and player number.
     *
     * @param sizeBoard the size of the board
     * @param playerNumber the number of players in the game
     */
    public void initializeGame(int sizeBoard, int playerNumber) {
        this.sizeBoard = sizeBoard;
        this.board = new Board(sizeBoard);
        humanPlayer = new Player(ColorPawn.PINK);

        switch (playerNumber) {
            case 2 -> {
                aiPlayer = new AIPlayer(ColorPawn.BLACK, new RandomAIStrategy(ColorPawn.BLACK));
                players = new Player[]{humanPlayer, aiPlayer};
            }
            case 3 -> {
                aiPlayer = new AIPlayer(ColorPawn.BLACK, new MinMaxAIStrategy());
                players = new Player[]{humanPlayer, aiPlayer};
            }
            case 4 -> {
                Player aiPlayer1 = new AIPlayer(ColorPawn.PINK, new RandomAIStrategy(ColorPawn.PINK));
                Player aiPlayer2 = new AIPlayer(ColorPawn.BLACK, new RandomAIStrategy(ColorPawn.BLACK));
                players = new Player[]{aiPlayer1, aiPlayer2};
            }
            default -> players = new Player[]{humanPlayer, playerNumber == 1 ? new Player(ColorPawn.BLACK) : aiPlayer};
        }

        this.totems = new Totem[]{new Totem(Mark.X), new Totem(Mark.O)};
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
            throw new IllegalStateException(e.getMessage());
        }

    }

        /**
     * Executes the AI's move for the specified totem.
     *
     * @param totemMove The move to be executed, containing the token and the new position for the totem.
     * @throws OxonoException If the current player is not an AI player or if the totem move is invalid.
     */
    private void executeTotemMove(Move totemMove) throws OxonoException {
        Token token = totemMove.token();
        Position newPosTotem = totemMove.movePosition();
        Position totemPos = getTotemPositionForMove(token.getMark());

        if (!board.isValidMove(newPosTotem, totemPos)) {
            throw new OxonoException("Invalid totem move");
        }

        Command command = new MoveTotemCommand(board, (Totem) token, newPosTotem, totemPos);
        invoker.executeCommand(command);
        lastTotemPlay = (Totem) token;
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

        Command command = new InsertPawnCommand(board, (Pawn) token, currentPlayer, totemPos, pawnPos);
        invoker.executeCommand(command);
        lastPawnPosition = pawnPos;

        notifyObservers(new OxonoEvent(ObservableEvent.PLACE_PAWN)
                .addData("pawn", token)
                .addData("newPosition", pawnPos)
                .addData("oldPosition", lastPawnPosition)
        );
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
 * @param input A string representing the move input. 
 *              Expected format: "totem [row] [column]" or "pawn [row] [column]"
 * @param isTotem A boolean flag indicating whether the move is for a totem (true) or a pawn (false)
 * @throws OxonoException If the input format is invalid or if the move is not allowed
 */
private void processInput(String input, boolean isTotem) throws OxonoException {
    String[] parts = input.split(" ");
    if (parts.length != 3) {
        throw new OxonoException("Invalid input format. Use 'totem [row] [column]' or 'pawn [row] [column]'");
    }

    try {
        Mark mark = Mark.valueOf(isTotem ? parts[0] : parts[0].substring(1));
        Position newPosition = new Position(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        Position actualTotemPos = getTotemPositionForMove(mark);

        Command command = isTotem ? processTotemMove(newPosition, actualTotemPos)
                : processPawnMove(mark, newPosition, actualTotemPos);
        invoker.executeCommand(command);
        if (isTotem) {
            notifyObservers(new OxonoEvent(ObservableEvent.MOVE_TOTEM)
                    .addData("totem", lastTotemPlay)
                    .addData("oldPosition", actualTotemPos)
                    .addData("newPosition", newPosition));
        } else {
            Pawn pawn = new Pawn(currentPlayer.getColor(), mark);
            notifyObservers(new OxonoEvent(ObservableEvent.PLACE_PAWN)
                    .addData("pawn", pawn)
                    .addData("oldPosition", lastPawnPosition)
                    .addData("newPosition", newPosition));
        }

    } catch (IllegalArgumentException e) {
        throw new OxonoException("Invalid input data");
    }
}

    private Command processTotemMove(Position newPosition, Position actualTotemPos) throws OxonoException {
        Totem totem = board.getTotem(actualTotemPos);

        if (!board.isValidMove(newPosition, actualTotemPos)) {
            throw new OxonoException("Invalid totem move");
        }

        lastTotemPlay = totem;
        updateTotemPosition(totem, newPosition);
        return new MoveTotemCommand(board, totem, newPosition, actualTotemPos);
    }

    private Command processPawnMove(Mark mark, Position newPosition, Position actualTotemPos) throws OxonoException {
        if (lastTotemPlay.getMark() != mark) {
            throw new OxonoException("Invalid mark, choose the pawn whose mark is equal to the totem moved");
        }

        if (!board.isValidInsertion(newPosition, actualTotemPos)) {
            throw new OxonoException("Invalid Pawn move");
        }
        lastPawnPosition = newPosition;
        Pawn pawn = new Pawn(currentPlayer.getColor(), mark);
        return new InsertPawnCommand(board, pawn, currentPlayer, actualTotemPos, newPosition);
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
    if (!board.isTotem(token)) {
        boolean isWin = board.checkWin((Pawn) token, lastPawnPosition);
        if (isWin) {
            state = GameState.GAME_OVER;
            notifyObservers(new OxonoEvent(ObservableEvent.WIN)
                    .addData("winner", currentPlayer)
                    .addData("winningPosition", board.getWinnigPositions())
            );
        }

        return isWin;
    }
    return false;
}


    public void switchPlayer() {
        currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
    }

    public boolean isDraw() {
        if (state != GameState.GAME_OVER) {
            boolean isDraw = players[0].dontHaveAnyPawns() && players[1].dontHaveAnyPawns();
            if (isDraw) {
                notifyObservers(new OxonoEvent(ObservableEvent.DRAW));
                state = GameState.GAME_OVER;
            }
            return isDraw;
        }
        return false;
    }


    public boolean undo() {
        if (state == GameState.GAME_OVER || !canUndo()) {
            return false;
        }

        Command command = invoker.undo();

        isUndoRedoInProgress = true;
        try {
            if (command instanceof InsertPawnCommand pawnCommand) {
                lastPawnPosition = pawnCommand.getPawnPosition();
                switchPlayer();

                if (invoker.getUndoPeek() instanceof MoveTotemCommand) {
                    lastTotemPlay = new Totem(invoker.getUndoPeek().getMovedMark());
                }

                state = GameState.WAITING_FOR_PAWN;

                notifyObservers(new OxonoEvent(ObservableEvent.UNDO)
                        .addData("pawn", pawnCommand.getPawn())
                        .addData("currentPlayer", currentPlayer)
                        .addData("newPosition", pawnCommand.getPawnPosition()));
            } else if (command instanceof MoveTotemCommand totemCommand) {
                Position oldPosition = totemCommand.getOldPosition();
                Totem totem = totemCommand.getTotem();
                updateTotemPosition(totem, oldPosition);
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

                state = GameState.WAITING_FOR_PAWN;

                notifyObservers(new OxonoEvent(ObservableEvent.REDO)
                        .addData("oldPosition", totemCommand.getOldPosition())
                        .addData("totem", lastTotemPlay)
                        .addData("newPosition", newPosition));
            } else if (command instanceof InsertPawnCommand pawnCommand) {
                lastPawnPosition = pawnCommand.getPawnPosition();
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

    public List<Position> getMovesPossilesForTotem(Position position) {
        return board.getMovesPossibles(position);
    }

    public boolean canUndo() {
        return invoker.canUndo() && !isUndoRedoInProgress;
    }

    public boolean canRedo() {
        return invoker.canRedo() && !isUndoRedoInProgress;
    }

    public boolean isUndoRedoInProgress() {
        return isUndoRedoInProgress;
    }


    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isCurrentPlayerAI() {
        state = GameState.AI_TURN;
        return currentPlayer instanceof AIPlayer;
    }

    public void surrender() {
        System.out.println("Player " + currentPlayer.getColor() + " surrendered! " +
                (currentPlayer == players[0] ? "AI wins!" : "Human wins!"));

        notifyObservers(new OxonoEvent(ObservableEvent.SURRENDER)
                .addData("PlayerWin", getOpponent()));
        state = GameState.SURRENDER;
    }

    public List<Position> positionsInsert(Position pos) {
        return board.getInsertionPositions(pos);
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(OxonoEvent event) {
        for (Observer observer : observers) {
            observer.update(this, event);
        }
    }

    public int[] getRemainingPawns() {
        int pawnsXplayerPink = players[0].getRemainingPawns(Mark.X);
        int pawns0playerPink = players[0].getRemainingPawns(Mark.O);
        int pawnsXplayerBlack = players[1].getRemainingPawns(Mark.X);
        int pawns0playerBlack = players[1].getRemainingPawns(Mark.O);
        return new int[]{pawnsXplayerPink, pawns0playerPink, pawnsXplayerBlack, pawns0playerBlack};
    }

    public GameState getGameState() {
        return state;
    }
    public static int size(){
        return sizeBoard;
    }
    public static Token getToken(Position pos) {
      return board.getToken(pos);
    }
    private Player getOpponent() {
        return currentPlayer == players[0] ? players[1] : players[0];
    }
}
