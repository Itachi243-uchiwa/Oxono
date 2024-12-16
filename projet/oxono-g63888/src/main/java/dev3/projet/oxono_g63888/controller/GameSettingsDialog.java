package dev3.projet.oxono_g63888.controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Objects;
import java.util.Optional;

public class GameSettingsDialog {
    public interface StartDialogCallback {
        void onGameSettingsChosen(int boardSize, int gameMode, String theme);
    }


    /**
     * Displays a dialog to configure game settings before starting a new game.
     * Allows the user to select the board size, game mode, and theme.
     * The selected settings are passed to the provided callback.
     *
     * @param callback The callback to handle the chosen game settings, including board size, game mode, and theme.
     */
    public static void showStartDialog(StartDialogCallback callback) {
        Platform.runLater(() -> {
            Dialog<GameSettings> dialog = new Dialog<>();
            dialog.setTitle("Nouvelle Partie");
            dialog.setHeaderText("Choisissez les paramètres de jeu");

            dialog.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(GameSettingsDialog.class.getResource("/styles/space-theme.css")).toExternalForm()
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

            ComboBox<String> theme = new ComboBox<>();
            theme.getItems().addAll(
                    "Default",
                    "Dark",
                    "Light",
                    "Retro",
                    "Vintage",
                    "Uchiwa",
                    "Futuristic",
                    "Colorblind",
                    "Luxury",
                    "Cartoon",
                    "Christmas",
                    "Cyberpunk",
                    "Space",
                    "Football",
                    "Jungle",
                    "Naruto vs Sasuke"
            );
            theme.setValue("Default");
            theme.getStyleClass().add("game-settings-combo");

            Label boardSizeLabel = new Label("Taille du plateau:");
            boardSizeLabel.getStyleClass().add("game-settings-label");

            Label gameModeLabel = new Label("Mode de jeu:");
            gameModeLabel.getStyleClass().add("game-settings-label");

            Label themeLabel = new Label("Thème:");
            themeLabel.getStyleClass().add("game-settings-label");

            grid.add(boardSizeLabel, 0, 0);
            grid.add(boardSize, 1, 0);
            grid.add(gameModeLabel, 0, 1);
            grid.add(gameMode, 1, 1);
            grid.add(themeLabel, 0, 2);
            grid.add(theme, 1, 2);

            dialog.getDialogPane().setContent(grid);

            dialog.getDialogPane().lookupButton(startButtonType).getStyleClass().add("dialog-button");
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("dialog-button");

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == startButtonType) {
                    return new GameSettings(
                            boardSize.getValue(),
                            gameMode.getSelectionModel().getSelectedIndex() + 1,
                            theme.getValue().toLowerCase()
                    );
                }
                return null;
            });

            Optional<GameSettings> result = dialog.showAndWait();
            result.ifPresent(settings ->
                    callback.onGameSettingsChosen(
                            settings.boardSize,
                            settings.gameMode,
                            settings.theme
                    )
            );
        });
    }

    /**
     * Displays a "Game Over" dialog with the provided message and options to start a new game or quit.
     *
     * @param message   The message to display in the dialog, indicating the reason for game completion.
     * @param onNewGame The callback to execute if the user selects the "New Game" option.
     * @param onQuit    The callback to execute if the user selects the "Quit" option.
     */
    public static void showGameOverDialog(String message, Runnable onNewGame, Runnable onQuit) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin de partie");
            alert.setHeaderText(null);
            alert.setContentText(message);

            alert.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(GameSettingsDialog.class.getResource("/styles/space-theme.css")).toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add("dialog-pane");

            ButtonType newGameButtonType = new ButtonType("Nouvelle partie", ButtonBar.ButtonData.OK_DONE);
            ButtonType quitButtonType = new ButtonType("Quitter", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(newGameButtonType, quitButtonType);

            alert.getDialogPane().lookupButton(newGameButtonType).getStyleClass().add("dialog-button");
            alert.getDialogPane().lookupButton(quitButtonType).getStyleClass().add("dialog-button");

            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(button -> {
                if (button == newGameButtonType) {
                    onNewGame.run();
                } else if (button == quitButtonType) {
                    onQuit.run();
                }
            });
        });
    }



    /**
     * Displays a confirmation dialog for quitting the game. If the user confirms, the provided
     * callback is executed.
     *
     * @param onConfirm The action to execute if the user confirms quitting the game.
     */
    public static void showQuitConfirmationDialog(Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quitter");
        alert.setHeaderText("Voulez-vous vraiment quitter le jeu?");
        alert.setContentText("Toute progression non sauvegardée sera perdue.");

        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(GameSettingsDialog.class.getResource("/styles/space-theme.css")).toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("dialog-pane");

        alert.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("dialog-button");
        alert.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("dialog-button");

        if (alert.showAndWait().get() == ButtonType.OK) {
            onConfirm.run();
        }
    }


    public static class GameSettings {
        final int boardSize;
        final int gameMode;
        final String theme;

        GameSettings(int boardSize, int gameMode, String theme) {
            this.boardSize = boardSize;
            this.gameMode = gameMode;
            this.theme = theme;
        }
    }
}