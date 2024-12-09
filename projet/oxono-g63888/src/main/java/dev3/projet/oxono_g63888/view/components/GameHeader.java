package dev3.projet.oxono_g63888.view.components;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.util.Arrays;

public class GameHeader {
    private HBox header;
    private Label playerTurnLabel;
    private Button undoButton;
    private Button redoButton;
    private Button surrenderButton;
    private Button quitButton;
    private Button aiMove;
    private ComboBox<String> themeSelector;

    public GameHeader() {
        createHeader();
    }

    private void createHeader() {
        header = new HBox(25);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header");

        playerTurnLabel = new Label("Tour actuel: Joueur Rose");
        playerTurnLabel.getStyleClass().add("player-turn-label");

        undoButton = new Button("Annuler");
        redoButton = new Button("Refaire");
        surrenderButton = new Button("Abandonner");
        quitButton = new Button("Quitter");
        aiMove = new Button("IA");

        createThemeSelector();

        for (Button btn : Arrays.asList(undoButton, redoButton, surrenderButton, quitButton, aiMove)) {
            btn.getStyleClass().add("game-button");
        }

        header.getChildren().addAll(playerTurnLabel, undoButton, redoButton, surrenderButton, quitButton, aiMove, themeSelector);
    }

    private void createThemeSelector() {
        themeSelector = new ComboBox<>();
        themeSelector.getItems().addAll(
                "Default", "Dark", "Light", "Retro", "Vintage", "Uchiwa",
                "Futuristic", "Colorblind", "Luxury", "Cartoon", "Christmas",
                "Cyberpunk", "Space", "Football", "Jungle", "Naruto vs Sasuke"
        );
        themeSelector.setValue("Classic");
        themeSelector.getStyleClass().add("theme-combo");
    }

    public HBox getRoot() {
        return header;
    }

    public Button getUndoButton() {
        return undoButton;
    }

    public Button getRedoButton() {
        return redoButton;
    }

    public Button getSurrenderButton() {
        return surrenderButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    public Button getAiMoveButton() {
        return aiMove;
    }

    public void setCurrentPlayer(String message) {
        playerTurnLabel.setText(message);
    }

    public ComboBox<String> getThemeSelector() {
        return themeSelector;
    }
}