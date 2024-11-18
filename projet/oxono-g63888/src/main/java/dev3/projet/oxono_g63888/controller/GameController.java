package dev3.projet.oxono_g63888.controller;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.Observer.Observer;
import dev3.projet.oxono_g63888.model.Observer.OxonoEvent;
import dev3.projet.oxono_g63888.view.GameView;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.List;
import java.util.Optional;

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

        // Désactiver les boutons au démarrage jusqu'à ce qu'une partie commence
        setButtonsState(false);
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

        Platform.runLater(() -> {
            game.playAITurn();
            if (game.checkWinCondition()) {
                handleGameOver(game.getCurrentPlayer());
            } else {
                game.switchPlayer();
                updateAIButtonState();
            }
        });
    }

    public void showStartDialog() {
        Dialog<GameSettings> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Partie");
        dialog.setHeaderText("Choisissez les paramètres de jeu");

        ButtonType startButtonType = new ButtonType("Commencer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(startButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<Integer> boardSize = new ComboBox<>();
        for (int i = 6; i <= 12; i++) boardSize.getItems().add(i);
        boardSize.setValue(6);

        ComboBox<String> gameMode = new ComboBox<>();
        gameMode.getItems().addAll(
                "Humain vs Humain",
                "Humain vs IA Aléatoire",
                "Humain vs IA MinMax",
                "IA vs IA"
        );
        gameMode.setValue("Humain vs Humain");

        grid.add(new Label("Taille du plateau:"), 0, 0);
        grid.add(boardSize, 1, 0);
        grid.add(new Label("Mode de jeu:"), 0, 1);
        grid.add(gameMode, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == startButtonType) {
                return new GameSettings(boardSize.getValue(), gameMode.getSelectionModel().getSelectedIndex() + 1);
            }
            return null;
        });

        Optional<GameSettings> result = dialog.showAndWait();
        result.ifPresent(settings -> {
            gameOver = false;
            game.initializeGame(settings.boardSize, settings.gameMode);
            setButtonsState(true);
            updateAIButtonState();
            view.setStatus("La partie commence! C'est au tour du joueur " +
                    (game.getCurrentPlayer().getColor() == ColorPawn.PINK ? "Rose" : "Noir"));
        });
    }

    public void handleCellClick(Position pos) {
        if (gameOver || game.isCurrentPlayerAI()) return;

        try {
            clickedPos = pos;
            Token token = game.getBoard().getToken(clickedPos);
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
                    view.setSelectedPosition(null);
                }
            }

            checkGameStateAfterMove(pawnMoved);

        } catch (OxonoException e) {
            handleMoveError(e.getMessage());
        }

        view.updateBoard(game.getBoard());
    }
    private void handleTotemSelection() {
        selectedTotemPosition = clickedPos;
        isPlacingTotem = true;
        view.setSelectedPosition(clickedPos);
        view.setStatus("Sélectionnez où déplacer le totem");
    }

    /**
     * Handles the movement of a selected totem to a specified position.
     *
     * @param pos The position where the selected totem will be moved.
     * @throws OxonoException If the move is invalid.
     */
    private void handleTotemMovement(Position pos) throws OxonoException {
        if (!game.getBoard().isValidMove(clickedPos, selectedTotemPosition)) {
            throw new OxonoException("Mouvement invalide");
        }

        game.processTotemInput(game.getBoard().getTotem(selectedTotemPosition).getMark() + " " + pos.row() + " " + pos.column());
        isPlacingTotem = false;
        selectedTotemPosition = clickedPos;
        List<Position> positions = game.positionsInsert(clickedPos);
        view.highlightPossibleMoves(positions, "hover-move");
        view.setSelectedPosition(null);
    }

    private boolean handlePawnPlacement(Position pos) throws OxonoException {
        if (game.getBoard().isValidInsertion(clickedPos, selectedTotemPosition)) {
            game.processPawnInput("R" + game.getBoard().getTotem(selectedTotemPosition).getMark() + " " + pos.row() + " " + pos.column());
            return true;
        }
        return false;
    }

    private void checkGameStateAfterMove(boolean pawnMoved) {
        if (game.checkWinCondition()) {
            handleGameOver(game.getCurrentPlayer());
        } else if (pawnMoved) {
            game.switchPlayer();
            if (game.isCurrentPlayerAI()) {
                updateAIButtonState();
            }
        }
    }

    private void handleMoveError(String message) {
        view.setStatus("Coup invalide: " + message);
        isPlacingTotem = false;
        selectedTotemPosition = null;
        view.setSelectedPosition(null);
    }

//    private void handleAITurn() {
//        Platform.runLater(() -> {
//            game.playAITurn();
//            if (game.checkWinCondition()) {
//                handleGameOver(game.getCurrentPlayer());
//            } else {
//                game.switchPlayer();
//            }
//        });
//    }

    private void handleAIvAIGame() {
        Thread aiThread = new Thread(() -> {
            while (!game.isDraw() && !game.checkWinCondition()) {
                Platform.runLater(() -> {
                    game.playAITurn();
                    if (!game.checkWinCondition()) {
                        game.switchPlayer();
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            Platform.runLater(() -> {
                if (game.checkWinCondition()) {
                    handleGameOver(game.getCurrentPlayer());
                } else {
                    handleGameOver(null);
                }
            });
        });
        aiThread.setDaemon(true);
        aiThread.start();
    }

    private void handleGameOver(Player winner) {
        gameOver = true;
        setButtonsState(false);
        String message = winner == null ?
                "Match nul!" :
                "Victoire du joueur " + (winner.getColor() == ColorPawn.PINK ? "Rose" : "Noir") + "!";
        showGameOverDialog(message);
    }

    private void handleUndo() {
        if (!gameOver) {
            game.undo();
            view.setStatus("Coup annulé");
            isPlacingTotem = false;
            selectedTotemPosition = null;
            view.setSelectedPosition(null);
            updateAIButtonState();
        }
    }

    private void handleRedo() {
        if (!gameOver) {
            game.redo();
            view.setStatus("Coup rétabli");
            updateAIButtonState();
        }
    }

    private void handleSurrender() {
        if (!gameOver) {
            game.surrender();
            handleGameOver(game.getCurrentPlayer().getColor() == ColorPawn.PINK ?
                    new Player(ColorPawn.BLACK) : new Player(ColorPawn.PINK));
        }
    }

    private void handleQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quitter");
        alert.setHeaderText("Voulez-vous vraiment quitter le jeu?");
        alert.setContentText("Toute progression non sauvegardée sera perdue.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }

    private void showGameOverDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de partie");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType newGame = new ButtonType("Nouvelle partie");
        ButtonType quit = new ButtonType("Quitter");
        alert.getButtonTypes().setAll(newGame, quit);

        alert.showAndWait().ifPresent(response -> {
            if (response == newGame) {
                showStartDialog();
            } else {
                Platform.exit();
            }
        });
    }

    @Override
    public void update(Game game, OxonoEvent event) {
        Platform.runLater(() -> {
            view.updateBoard(game.getBoard());
            view.updateRacks(game.getRemainingPawns());

            switch (event.getEvent()) {
                case GAME_START -> {
                    setButtonsState(true);
                    updateAIButtonState();
                    view.setStatus("La partie commence!");
                }
                case MOVE_TOTEM -> view.setStatus("Totem déplacé");
                case PLACE_PAWN -> view.setStatus("Pion placé");
                case WIN -> handleGameOver(game.getCurrentPlayer());
                case DRAW -> handleGameOver(null);
                case UNDO -> view.setStatus("Coup annulé");
                case REDO -> view.setStatus("Coup rétabli");
            }

            updatePlayerTurn(game.getCurrentPlayer());
        });
    }
    private void updatePlayerTurn(Player currentPlayer) {
        view.setStatus("Tour du joueur " +
                (currentPlayer.getColor() == ColorPawn.PINK ? "Rose" : "Noir"));
    }


    private static class GameSettings {
        final int boardSize;
        final int gameMode;

        GameSettings(int boardSize, int gameMode) {
            this.boardSize = boardSize;
            this.gameMode = gameMode;
        }
    }
}