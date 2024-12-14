package dev3.projet.oxono_g63888.controller;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.Observer.Observer;
import dev3.projet.oxono_g63888.model.Observer.OxonoEvent;
import dev3.projet.oxono_g63888.view.GameView;
import javafx.application.Platform;
import javafx.scene.control.*;

import java.util.List;

public class GameController implements Observer {
    private Game game;
    private GameView view;
    private Position selectedTotemPosition;
    private boolean isPlacingTotem;
    private Position clickedPos;
    private boolean gameOver;
    private Button aiMove;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        this.gameOver = false;
        view.setController(this);
        game.registerObserver(this);
        aiMove = view.getAiMoveButton();
        setupEventHandlers();
    }

    /**
     * Sets up event handlers for buttons and initializes the game state.
     */
    private void setupEventHandlers() {
        view.getUndoButton().setOnAction(e -> handleUndo());
        view.getRedoButton().setOnAction(e -> handleRedo());
        view.getSurrenderButton().setOnAction(e -> handleSurrender());
        view.getQuitButton().setOnAction(e -> handleQuit());
        aiMove.setOnAction(e -> performAIMove());

        setButtonsState(false);
    }

    /**
     * Displays a dialog to configure game settings before starting a new game.
     */
    public void showStartDialog() {
        GameSettingsDialog.showStartDialog((boardSize, gameMode, theme) -> {
            gameOver = false;
            game.initializeGame(boardSize, gameMode);
            view.updateTheme(theme);
            setButtonsState(true);
            updateAIButtonState();
            view.setStatus("The game starts! It's player " +
                    (game.getColorPlayer() == ColorPawn.PINK ? "Pink" : "Black") + "'s turn.");
        });
    }

    /**
     * Handles the end of the game, displaying the winner and offering options to restart or quit.
     *
     * @param winner   The winning player, or null if the game is a draw.
     * @param surround True if the game ended due to a surrender.
     */
    private void handleGameOver(String winner, boolean surround) {
        gameOver = true;
        setButtonsState(false);
        String message = (winner == null) ?
                "Draw!" :
                "Player " + winner + " wins!";
        if (surround) {
            message = winner + " has won the game by surrender!";
        }

        GameSettingsDialog.showGameOverDialog(
                message,
                this::showStartDialog,
                Platform::exit
        );
    }

    /**
     * Displays a confirmation dialog for quitting the game.
     */
    private void handleQuit() {
        GameSettingsDialog.showQuitConfirmationDialog(Platform::exit);
    }

    /**
     * Updates the enabled state of game buttons based on the game status.
     *
     * @param enabled True to enable the buttons, false to disable them.
     */
    private void setButtonsState(boolean enabled) {
        view.getUndoButton().setDisable(!enabled || gameOver);
        view.getRedoButton().setDisable(!enabled || gameOver);
        view.getSurrenderButton().setDisable(!enabled || gameOver);
        aiMove.setDisable(!enabled || gameOver || !game.isCurrentPlayerAI());
    }

    /**
     * Updates the enabled state of the AI move button based on the current player's type.
     */
    private void updateAIButtonState() {
        aiMove.setDisable(!game.isCurrentPlayerAI());
    }

    /**
     * Performs an AI move if it is the AI's turn and the game is not over.
     */
    private void performAIMove() {
        if (gameOver || !game.isCurrentPlayerAI()) return;
        game.playAITurn();
        updateAIButtonState();
    }

    /**
     * Handles the logic for selecting or moving a game piece when a cell is clicked.
     *
     * @param pos The position of the clicked cell.
     */
    public void handleCellClick(Position pos) {
        if (gameOver || game.isCurrentPlayerAI()) {
            view.setStatus("It's the AI's turn to play.");
            return;
        }

        try {
            clickedPos = pos;
            Token token = game.getToken(clickedPos);

            if (token instanceof Totem) {
                if (selectedTotemPosition != null && !isPlacingTotem) {
                    view.setStatus("You must place a pawn before moving another totem!");
                    return;
                }
                handleTotemSelection();
            } else if (isPlacingTotem && selectedTotemPosition != null) {
                handleTotemMovement(pos);
                view.setStatus("Select where to place your pawn.");
            } else if (selectedTotemPosition != null) {
                boolean pawnMoved = handlePawnPlacement(pos);
                if (pawnMoved) {
                    selectedTotemPosition = null;
                    isPlacingTotem = false;
                    if (game.isCurrentPlayerAI()) {
                        updateAIButtonState();
                    }
                }
            }

        } catch (OxonoException e) {
            handleMoveError(e.getMessage());
        }
    }

    /**
     * Handles the selection of a totem piece.
     */
    private void handleTotemSelection() {
        selectedTotemPosition = clickedPos;
        List<Position> positions = game.getMovesPossilesForTotem(selectedTotemPosition);
        view.highlightPossibleMoves(positions, "valid-move");
        isPlacingTotem = true;
        view.setStatus("Select where to move the totem.");
    }

    /**
     * Handles the movement of a totem piece to a new position.
     *
     * @param pos The new position for the totem.
     * @throws OxonoException If the move is invalid.
     */
    private void handleTotemMovement(Position pos) throws OxonoException {
        Mark mark = game.getMarkTotem(selectedTotemPosition);
        game.processTotemInput(mark + " " + pos.row() + " " + pos.column());
        isPlacingTotem = false;
        selectedTotemPosition = clickedPos;
        List<Position> positions = game.positionsInsert(clickedPos);
        view.highlightPossibleMoves(positions, "hover-move");
    }

    /**
     * Handles the placement of a pawn on the board.
     *
     * @param pos The position to place the pawn.
     * @return True if the pawn was successfully placed, false otherwise.
     * @throws OxonoException If the move is invalid.
     */
    private boolean handlePawnPlacement(Position pos) throws OxonoException {
        Mark mark = game.getMarkTotem(selectedTotemPosition);
        game.processPawnInput("R" + mark + " " + pos.row() + " " + pos.column());
        return true;
    }

    /**
     * Handles errors that occur during a move.
     *
     * @param message The error message to display.
     */
    private void handleMoveError(String message) {
        view.setStatus("Invalid move: " + message);
        isPlacingTotem = false;
        selectedTotemPosition = null;
    }

    /**
     * Handles the undo operation to revert the last move.
     */
    private void handleUndo() {
        if (!gameOver) {
            boolean undo = game.undo();
            if (undo) {
                view.setStatus("Move undone.");
                updateAIButtonState();
                isPlacingTotem = false;
                selectedTotemPosition = null;
            } else {
                view.setStatus("No moves to undo.");
            }
        }
    }

    /**
     * Handles the redo operation to reapply the last undone move.
     */
    private void handleRedo() {
        if (!gameOver) {
            boolean redo = game.redo();
            if (redo) {
                updateAIButtonState();
            } else {
                view.setStatus("No moves to redo.");
            }
        }
    }

    /**
     * Handles the surrender action, ending the game with the opponent as the winner.
     */
    private void handleSurrender() {
        game.surrender();
    }

    /**
     * Updates the game view in response to game state changes.
     *
     * @param game  The current game instance.
     * @param event The event that triggered the update.
     */
    @Override
    public void update(Game game, OxonoEvent event) {
        Platform.runLater(() -> {
            view.updateRacks(game.getRemainingPawns());
            switch (event.getEvent()) {
                case GAME_START -> {
                    setButtonsState(true);
                    updateAIButtonState();
                    view.displayBoard(game);
                    view.setStatus("La partie commence!");

                }
                case MOVE_TOTEM -> {
                    Position newPos = event.getEventData("newPosition", Position.class);
                    Position oldPos = event.getEventData("oldPosition", Position.class);
                    Totem totem = event.getEventData("totem", Totem.class);
                    view.updateTotem(newPos, totem, oldPos);
                    view.setStatus("Totem déplacé");
                }
                case PLACE_PAWN -> {
                    Position pawnPos = event.getEventData("newPosition", Position.class);
                    Pawn pawn = event.getEventData("pawn", Pawn.class);
                    view.updatePawn(pawnPos, pawn);
                    view.setStatus("Pion placé");
                }
                case WIN -> {
                    handleGameOver(game.getToString(), false);
                    view.setStatus("Victoire du joueur " + game.getToString());
                    List<Position> fourSuite = event.getEventData("winningPosition", List.class);
                    System.out.println(fourSuite);
                    view.addVictoryAnimation(fourSuite);

                }
                case DRAW -> {
                    handleGameOver(null, false);
                }
                case SURRENDER -> {
                    Player playerWin = event.getEventData("PlayerWin", Player.class);
                    handleGameOver(playerWin.toString(), true);
                }
                case UNDO -> {
                    try {

                        Pawn pawn = event.getEventData("pawn", Pawn.class);
                        Totem totem = event.getEventData("totem", Totem.class);
                        Position newPosition = event.getEventData("newPosition", Position.class);
                        Position oldPosition = event.getEventData("oldPosition", Position.class);

                        if (pawn != null && newPosition != null) {
                            view.clearCell(newPosition);
                            view.setStatus("Annulation : pion retiré");
                        } else if (totem != null && oldPosition != null && newPosition != null) {
                            view.updateTotem(oldPosition, totem, newPosition);
                            view.setStatus("Annulation : totem déplacé");
                        }
                    } catch (OxonoException e) {
                        view.setStatus("Erreur lors de l'annulation");
                    }
                    view.clearColor("hover-move");
                }
                case REDO -> {
                    Pawn pawn = event.getEventData("pawn", Pawn.class);
                    Totem totem = event.getEventData("totem", Totem.class);
                    Position oldPosition = event.getEventData("oldPosition", Position.class);
                    Position newPosition = event.getEventData("newPosition", Position.class);


                    if (pawn != null && newPosition != null) {
                        view.updatePawn(newPosition, pawn);
                        view.setStatus("Coup rétabli : pion ajouté");
                    } else if (totem != null && oldPosition != null && newPosition != null) {
                        view.updateTotem(newPosition, totem, oldPosition);
                        view.setStatus("Coup rétabli : totem déplacé");
                    }
                }


            }

            updatePlayerTurn(game.getToString());
        });
    }

    private void updatePlayerTurn(String currentPlayer) {
        view.setCurrentPlayer("Tour du joueur " +
                (currentPlayer));
    }

}