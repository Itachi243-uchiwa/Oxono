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
        scene = new Scene(root, 1000, 1000);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/default-theme.css")).toExternalForm());
        this.themeManager = new ThemeManager(scene);
    }

    /**
     * Sets the controller for the game view and delegates the controller assignment to the board grid.
     *
     * @param controller the GameController instance to be*/
    public void setController(GameController controller) {
        this.controller = controller;
        this.boardGrid.setController(controller);
    }

    /**
     * Creates the main view structure for the game interface.
     * Initializes and arranges the UI layout, including the header,
     */
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


    /**
     * Displays the current state of the game board on the user interface.
     *
     * @param game the current game instance containing the state*/
    public void displayBoard(Game game) {
        boardGrid.displayBoard(game, cellMap);
    }

    /**
     *
     */
    public void updatePawn(Position position, Pawn pawn) {
        boardGrid.updatePawn(position, pawn, cellMap);
    }

    /**
     * Updates the position of a totem on the game board, visually reflecting the change,
     * and clears the visual representation of the*/
    public void updateTotem(Position position, Totem totem, Position oldpos) {
        boardGrid.updateTotem(position, totem, oldpos, cellMap);
    }

    /**
     * Removes the specified color style from all cells in the game view.
     *
     */
    public void clearColor(String color) {
        boardGrid.clearColor(color, cellMap);
    }


    /**
     * Highlights the possible moves on the game board by applying a specific color
     * to the cells corresponding to the given list of positions.
     *
     * @param positions a list of positions on the board that represent the cells to be highlighted
     * @param color the style class or color identifier to apply to the cells
     */
    public void highlightPossibleMoves(List<Position> positions, String color) {
        boardGrid.highlightPossibleMoves(positions, color, cellMap);
    }


    /**
     * Updates the player's racks to reflect the remaining pawns for each color.
     *
     * @param remainingPawns an array of integers where each pair represents the count of small and large pawns
     *                        remaining for a specific color. The first pair corresponds to pink pawns, and the
     *                        second pair corresponds to black pawns.
     */
    public void updateRacks(int[] remainingPawns) {
        playerRacks.updateRacks(remainingPawns);
    }


    /**
     * Clears the content of a specific cell in the game board.
     * This method delegates the clearing operation to the underlying grid and updates the visual representation
     * of the cell associated with the given position.
     *
     * @param position The position of the cell to be cleared.
     */
    public void clearCell(Position position) {
        boardGrid.clearCell(position, cellMap);
    }

    /**
     * Updates the status label with the provided message.
     *
     * @param message the message to be displayed in the status label
     */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Retrieves the undo button used to undo the last undone action.
     *
     * @return The undo button.
     */
    public Button getUndoButton() {
        return header.getUndoButton();
    }

    /**
     * Retrieves the redo button used to redo the last undone action.
     *
     * @return The redo button.
     */
    public Button getRedoButton() {
        return header.getRedoButton();
    }

    public Button getSurrenderButton() {
        return header.getSurrenderButton();
    }

    /**
     * Retrieves the "Quit" button from the header component of the game view.
     * This button is generally used to trigger the*/
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