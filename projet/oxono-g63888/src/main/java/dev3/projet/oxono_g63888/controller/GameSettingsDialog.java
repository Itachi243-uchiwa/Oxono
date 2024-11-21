package dev3.projet.oxono_g63888.controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Objects;
import java.util.Optional;

public class GameSettingsDialog {
    public interface StartDialogCallback {
        void onGameSettingsChosen(int boardSize, int gameMode);
    }

    public interface GameOverDialogCallback {
        void onNewGame();
        void onQuit();
    }
    public static void showStartDialog(StartDialogCallback callback) {
        Platform.runLater(() -> {
            Dialog<GameSettings> dialog = new Dialog<>();
            dialog.setTitle("Nouvelle Partie");
            dialog.setHeaderText("Choisissez les paramètres de jeu");

            // Ajout du fichier CSS au dialog
            dialog.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(GameSettingsDialog.class.getResource("/styles/styles.css")).toExternalForm()
            );
            dialog.getDialogPane().getStyleClass().add("dialog-pane");

            ButtonType startButtonType = new ButtonType("Commencer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(startButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            grid.getStyleClass().add("game-settings-grid");

            ComboBox<Integer> boardSize = new ComboBox<>();
            for (int i = 6; i <= 12; i++) boardSize.getItems().add(i);
            boardSize.setValue(6);
            boardSize.getStyleClass().add("game-settings-combo");

            ComboBox<String> gameMode = new ComboBox<>();
            gameMode.getItems().addAll(
                    "Humain vs Humain",
                    "Humain vs IA Aléatoire",
                    "Humain vs IA MinMax",
                    "IA vs IA"
            );
            gameMode.setValue("Humain vs Humain");
            gameMode.getStyleClass().add("game-settings-combo");

            Label boardSizeLabel = new Label("Taille du plateau:");
            boardSizeLabel.getStyleClass().add("game-settings-label");

            Label gameModeLabel = new Label("Mode de jeu:");
            gameModeLabel.getStyleClass().add("game-settings-label");

            grid.add(boardSizeLabel, 0, 0);
            grid.add(boardSize, 1, 0);
            grid.add(gameModeLabel, 0, 1);
            grid.add(gameMode, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.getDialogPane().lookupButton(startButtonType).getStyleClass().add("dialog-button");
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("dialog-button");

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == startButtonType) {
                    return new GameSettings(boardSize.getValue(), gameMode.getSelectionModel().getSelectedIndex() + 1);
                }
                return null;
            });

            Optional<GameSettings> result = dialog.showAndWait();
            result.ifPresent(settings ->
                    callback.onGameSettingsChosen(settings.boardSize, settings.gameMode)
            );
        });
    }

    public static void showGameOverDialog(String message, GameOverDialogCallback callback) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin de partie");
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Ajouter le fichier CSS
            alert.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(GameSettingsDialog.class.getResource("/styles/styles.css")).toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("dialog-pane");

            ButtonType newGame = new ButtonType("Nouvelle partie");
            ButtonType quit = new ButtonType("Quitter");
            alert.getButtonTypes().setAll(newGame, quit);

            // Style des boutons
            alert.getDialogPane().lookupButton(newGame).getStyleClass().add("dialog-button");
            alert.getDialogPane().lookupButton(quit).getStyleClass().add("dialog-button");

            alert.showAndWait().ifPresent(response -> {
                if (response == newGame) {
                    callback.onNewGame();
                } else {
                    callback.onQuit();
                }
            });
        });
    }

    public static void showQuitConfirmationDialog(Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quitter");
        alert.setHeaderText("Voulez-vous vraiment quitter le jeu?");
        alert.setContentText("Toute progression non sauvegardée sera perdue.");

        // Ajouter le fichier CSS
        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(GameSettingsDialog.class.getResource("/styles/styles.css")).toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("dialog-pane");

        // Style des boutons
        alert.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("dialog-button");
        alert.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("dialog-button");

        if (alert.showAndWait().get() == ButtonType.OK) {
            onConfirm.run();
        }
    }


    public static class GameSettings {
        final int boardSize;
        final int gameMode;

        GameSettings(int boardSize, int gameMode) {
            this.boardSize = boardSize;
            this.gameMode = gameMode;
        }
    }
}