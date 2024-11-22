package dev3.projet.oxono_g63888.controller;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.Observer.Observer;
import dev3.projet.oxono_g63888.model.Observer.OxonoEvent;
import dev3.projet.oxono_g63888.view.GameView;
import javafx.application.Platform;
import javafx.scene.control.*;

import java.util.List;

import static dev3.projet.oxono_g63888.model.Observer.ObservableEvent.GAME_START;

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

    private void setupEventHandlers() {
        view.getUndoButton().setOnAction(e -> handleUndo());
        view.getRedoButton().setOnAction(e -> handleRedo());
        view.getSurrenderButton().setOnAction(e -> handleSurrender());
        view.getQuitButton().setOnAction(e -> handleQuit());
        aiMove.setOnAction(e -> performAIMove());

        setButtonsState(false);
    }

    // [Previous methods remain the same]

    public void showStartDialog() {
        GameSettingsDialog.showStartDialog((boardSize, gameMode) -> {
            gameOver = false;
            game.initializeGame(boardSize, gameMode);
            setButtonsState(true);
            updateAIButtonState();
            view.setStatus("La partie commence! C'est au tour du joueur " +
                    (game.getCurrentPlayer().getColor() == ColorPawn.PINK ? "Rose" : "Noir"));
        });
    }

    private void handleGameOver(Player winner, boolean surround) {
        gameOver = true;
        setButtonsState(false);
        String message = winner == null ?
                "Match nul!" :
                "Victoire du joueur " + (winner) + "!";
        if (surround) {
            message = (winner) + " a gagné la partie! par Abandon";
        }

        GameSettingsDialog.showGameOverDialog(message, new GameSettingsDialog.GameOverDialogCallback() {
            @Override
            public void onNewGame() {
                showStartDialog();
            }

            @Override
            public void onQuit() {
                Platform.exit();
            }
        });
    }

    private void handleQuit() {
        GameSettingsDialog.showQuitConfirmationDialog(Platform::exit);
    }


    private void setButtonsState(boolean enabled) {
        view.getUndoButton().setDisable(!enabled || gameOver);
        view.getRedoButton().setDisable(!enabled || gameOver);
        view.getSurrenderButton().setDisable(!enabled || gameOver);
        aiMove.setDisable(!enabled || gameOver || !game.isCurrentPlayerAI());
    }

    private void updateAIButtonState() {
        aiMove.setDisable(!game.isCurrentPlayerAI());
    }

    private void performAIMove() {
        if (gameOver || !game.isCurrentPlayerAI()) return;
        game.playAITurn();
    }

    public void handleCellClick(Position pos) {
        if (gameOver || game.isCurrentPlayerAI()) return;

        try {
            clickedPos = pos;
            Token token = game.getToken(clickedPos);
            boolean pawnMoved = false;

            if (token instanceof Totem) {
                if (selectedTotemPosition != null && !isPlacingTotem) {
                    view.setStatus("Vous devez d'abord placer un pion avant de déplacer un autre totem!");
                    return;
                }
                handleTotemSelection();
            } else if (isPlacingTotem && selectedTotemPosition != null) {
                handleTotemMovement(pos);
                view.setStatus("Sélectionnez où placer votre pion");
            } else if (selectedTotemPosition != null) {
                pawnMoved = handlePawnPlacement(pos);
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

    private void handleTotemSelection() {
        selectedTotemPosition = clickedPos;
        List<Position> positions = game.getMovesPossilesForTotem(selectedTotemPosition);
        view.highlightPossibleMoves(positions, "valid-move");
        isPlacingTotem = true;
        view.setStatus("Sélectionnez où déplacer le totem");
    }

    private void handleTotemMovement(Position pos) throws OxonoException {
        Mark mark = game.getMarkTotem(selectedTotemPosition);
        game.processTotemInput( mark + " " + pos.row() + " " + pos.column());
        isPlacingTotem = false;
        selectedTotemPosition = clickedPos;
        List<Position> positions = game.positionsInsert(clickedPos);
        view.highlightPossibleMoves(positions, "hover-move");

    }

    private boolean handlePawnPlacement(Position pos) throws OxonoException {
        Mark mark = game.getMarkTotem(selectedTotemPosition);
        game.processPawnInput("R" + mark + " " + pos.row() + " " + pos.column());
        return true;
    }

    private void handleMoveError(String message) {
        view.setStatus("Coup invalide: " + message);
        isPlacingTotem = false;
        selectedTotemPosition = null;
    }
        private void handleUndo() {
        if (!gameOver) {
            boolean undo = game.undo();
            if (undo) {
                view.setStatus("Coup annulé");
                updateAIButtonState();
                isPlacingTotem = false;
                selectedTotemPosition = null;
                updateAIButtonState();
            } else {
                view.setStatus("Aucun coup à annuler");
            }
        }
    }

    private void handleRedo() {
        if (!gameOver) {
            boolean redo = game.redo();
            if (redo){
            updateAIButtonState();
        } else {
            view.setStatus("Aucun coup à retablir ");
            }
        }
    }

    private void handleSurrender() {
            game.surrender();
    }
    @Override
    public void update(Game game, OxonoEvent event) {
        Platform.runLater(() -> {
            view.updateRacks(game.getRemainingPawns());
            switch (event.getEvent()) {
                case GAME_START -> {
                    setButtonsState(true);
                    updateAIButtonState();
                    view.updateBoard(game);
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
                    handleGameOver(game.getCurrentPlayer(), false);
                    view.setStatus("Victoire du joueur " + game.getCurrentPlayer());
                    List<Position> fourSuite = event.getEventData("winningPosition", List.class);
                    System.out.println(fourSuite);
                    view.addVictoryAnimation(fourSuite);

                }
                case DRAW -> {
                    handleGameOver(null, false);
                    System.out.println("ici 4");
                }
                case SURRENDER -> {
                    Player playerWin = event.getEventData("PlayerWin", Player.class);
                    handleGameOver(playerWin, true);
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

            updatePlayerTurn(game.getCurrentPlayer());
        });
    }

    private void updatePlayerTurn(Player currentPlayer) {
        view.setCurrentPlayer("Tour du joueur " +
                (currentPlayer.toString()));
    }

}