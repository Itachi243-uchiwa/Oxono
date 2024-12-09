package dev3.projet.oxono_g63888.view;

import dev3.projet.oxono_g63888.controller.GameController;
import dev3.projet.oxono_g63888.model.Game;
import dev3.projet.oxono_g63888.model.Pawn;
import dev3.projet.oxono_g63888.model.Position;
import dev3.projet.oxono_g63888.model.Totem;
import dev3.projet.oxono_g63888.view.components.*;
import dev3.projet.oxono_g63888.view.utils.ThemeManager;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.*;

public class GameView {
    private BoardGrid boardGrid;
    private VBox root;
    private Label statusLabel;
    private GameHeader header;
    private PlayerRacks playerRacks;
    private GameController controller;
    private Map<Position, StackPane> cellMap;
    private ThemeManager themeManager;
    private Scene scene;

    public GameView() {
        cellMap = new HashMap<>();
        createView();
        scene = new Scene(getRoot(), 1000, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/default-theme.css")).toExternalForm());
        this.themeManager = new ThemeManager(scene);
    }

    public void setController(GameController controller) {
        this.controller = controller;
        this.boardGrid.setController(controller);
    }

    private void createView() {
        root = new VBox(30);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        VBox gameContainer = new VBox(20);
        gameContainer.setAlignment(Pos.CENTER);

        header = new GameHeader();
        boardGrid = new BoardGrid();
        playerRacks = new PlayerRacks();

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        ComboBox<String> selectorTheme = header.getThemeSelector();
        selectorTheme.setOnAction(event-> updateTheme(selectorTheme.getValue()) );

        gameContainer.getChildren().addAll(header.getRoot(), boardGrid.getRoot(), playerRacks.getRoot(), statusLabel);
        root.getChildren().add(gameContainer);
    }


    public void displayBoard(Game game) {
        boardGrid.displayBoard(game, cellMap);
    }

    public void updatePawn(Position position, Pawn pawn) {
        boardGrid.updatePawn(position, pawn, cellMap);
    }

    public void updateTotem(Position position, Totem totem, Position oldpos) {
        boardGrid.updateTotem(position, totem, oldpos, cellMap);
    }

    public void clearColor(String color) {
        boardGrid.clearColor(color, cellMap);
    }

    public void highlightPossibleMoves(List<Position> positions, String color) {
        boardGrid.highlightPossibleMoves(positions, color, cellMap);
    }

    public void updateRacks(int[] remainingPawns) {
        playerRacks.updateRacks(remainingPawns);
    }

    public void clearCell(Position position) {
        boardGrid.clearCell(position, cellMap);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    public VBox getRoot() {
        return root;
    }

    public Button getUndoButton() {
        return header.getUndoButton();
    }

    public Button getRedoButton() {
        return header.getRedoButton();
    }

    public Button getSurrenderButton() {
        return header.getSurrenderButton();
    }

    public Button getQuitButton() {
        return header.getQuitButton();
    }

    public Button getAiMoveButton() {
        return header.getAiMoveButton();
    }

    public void setCurrentPlayer(String message) {
        header.setCurrentPlayer(message);
    }

    public void addVictoryAnimation(List<Position> pos) {
        boardGrid.addVictoryAnimation(pos, cellMap);
    }

    public void updateTheme(String theme) {
        themeManager.setTheme(theme);
    }

    public Scene getScene() {
        return scene;
    }

}