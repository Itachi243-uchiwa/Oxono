package dev3.projet.oxono_g63888.view.components;

import dev3.projet.oxono_g63888.controller.GameController;
import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.view.utils.GameAnimation;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

public class BoardGrid {
    private GridPane grid;
    private GameController controller;

    public BoardGrid() {
        createBoardGrid();
    }

    private void createBoardGrid() {
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.getStyleClass().add("board-container");
    }

    /**
     * Sets the game controller for this board grid.
     *
     * @param controller the GameController instance to be associated with this board grid
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Retrieves the root GridPane of the BoardGrid.
     *
     * @return the GridPane representing the root of the board grid.
     */
    public GridPane getRoot() {
        return grid;
    }

    /**
     * Displays the game board by updating the visual grid and mapping positions to their respective graphical components.
     * It clears any existing content in the grid and cellMap, then populates them with the current state of the game board.
     *
     * @param game The game instance containing the current state and tokens on the board.
     * @param cellMap A map that associates each board position with its corresponding StackPane graphical component.
     */
    public void displayBoard(Game game, Map<Position, StackPane> cellMap) {
        grid.getChildren().clear();
        cellMap.clear();

        for (int i = 0; i < Game.size(); i++) {
            for (int j = 0; j < Game.size(); j++) {
                StackPane cell = createBoardCell();
                Position pos = new Position(i, j);
                Token token = game.getToken(pos);

                if (token != null) {
                    if (token instanceof Totem) {
                        TokenGraphic totemFX = createTotemFX((Totem) token);
                        cell.getChildren().add(totemFX);
                    } else {
                        TokenGraphic pawnFX = createPawnFX((Pawn) token);
                        cell.getChildren().add(pawnFX);
                    }
                }

                cell.setOnMouseClicked(e -> {
                    if (controller != null) {
                        controller.handleCellClick(pos);
                    }
                });

                grid.add(cell, j, i);
                cellMap.put(pos, cell);
            }
        }
    }

    /**
     * Updates the visual representation of a Pawn on the board grid at a specified position.
     * This method clears any hover-move styling present in the cell map, creates a new visual
     * representation for the Pawn, animates its placement, and updates the associated cell with the new graphic.
     *
     * @param position The position of the cell on the board where the Pawn should be updated.
     * @param pawn The Pawn to be placed on the specified position, including its color and mark.
     * @param cellMap A map storing the relationship between board positions and their respective cell representations.
     */
    public void updatePawn(Position position, Pawn pawn, Map<Position, StackPane> cellMap) {
        clearColor("hover-move", cellMap);
        StackPane cell = cellMap.get(position);

        TokenGraphic pawnFx = createPawnFX(pawn);
        GameAnimation.addPawnDropAnimation(pawnFx);

        cell.getChildren().setAll(pawnFx);
        cellMap.put(position, cell);
    }

    /**
     * Updates the position of a {@link Totem} on the game board, clears the old position, and visually places
     * the totem at the new position with appropriate animations.
     *
     * @param position  The new position where the totem is to be placed.
     * @param totem     The totem being moved.
     * @param oldpos    The previous position of the totem that needs to be cleared.
     * @param cellMap   A mapping of board positions to their corresponding {@link StackPane} elements.
     */
    public void updateTotem(Position position, Totem totem, Position oldpos, Map<Position, StackPane> cellMap) {
        clearColor("valid-move", cellMap);

        StackPane oldCell = cellMap.get(oldpos);
        oldCell.getChildren().clear();

        StackPane newCell = cellMap.get(position);
        TokenGraphic totemFX = createTotemFX(totem);

        GameAnimation.addTotemBounceAnimation(totemFX);
        newCell.getChildren().setAll(totemFX);
        cellMap.put(oldpos, oldCell);
        cellMap.put(position, newCell);
    }

    /**
     * Creates a graphical representation of a pawn (TokenGraphic) with corresponding color and mark.
     *
     * @param pawn The Pawn object for which the graphical representation is created.
     *             Its color is based on the Pawn's ColorPawn property and its mark is set using the mark of the pawn.
     * @return A TokenGraphic instance representing the Pawn with the appropriate color and mark.
     */
    private TokenGraphic createPawnFX(Pawn pawn) {
        Color color = pawn.getColor() == ColorPawn.PINK ? Color.DEEPPINK : Color.BLACK;
        TokenGraphic pawnFX = new TokenGraphic(color, TokenGraphic.PieceType.PAWN);
        pawnFX.setMark(pawn.getMark());
        return pawnFX;
    }

    /**
     * Creates a graphical representation of a totem token.
     *
     * @param totem the totem token from which the graphical representation will be created
     * @return a TokenGraphic object representing the given totem
     */
    private TokenGraphic createTotemFX(Totem totem) {
        TokenGraphic totemFX = new TokenGraphic(Color.CYAN, TokenGraphic.PieceType.TOTEM);
        totemFX.setMark(totem.getMark());
        return totemFX;
    }

    /**
     * Creates and returns a new StackPane instance styled as a board cell.
     * The created cell will have the "board-cell" style class added to it.
     *
     * @return a StackPane representing a single board cell
     */
    private StackPane createBoardCell() {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("board-cell");
        return cell;
    }

    /**
     * Removes the specified color style from all the cells in the provided cell map.
     *
     * @param color   The name of the style class to be removed from the cells.
     * @param cellMap A map linking positions to StackPane objects representing the cells.
     */
    public void clearColor(String color, Map<Position, StackPane> cellMap) {
        cellMap.values().forEach(cell ->
                cell.getStyleClass().removeIf(style -> style.equals(color))
        );
    }

    /**
     * Highlights the possible moves on the board by applying a specific color to the corresponding cells.
     * Updates the visual representation of the board by adding the provided color style to the grid cells
     * associated with the given positions.
     *
     * @param positions a list of positions on the board that represent the possible moves to be highlighted
     * @param color the style class or color name to apply to the target cells
     * @param cellMap a mapping between board positions and their corresponding StackPane elements
     */
    public void highlightPossibleMoves(List<Position> positions, String color, Map<Position, StackPane> cellMap) {
        clearColor(color, cellMap);
        positions.forEach(pos -> {
            StackPane cell = cellMap.get(pos);
            if (cell != null) {
                cell.getStyleClass().add(color);
            }
        });
    }

    /**
     * Clears the content of a specific cell in the grid.
     * Removes all children elements from the cell corresponding to the given position in the cell map.
     *
     * @param position The position of the cell to be cleared.
     * @param cellMap A map that associates a Position to its corresponding StackPane, representing the cells of the grid.
     */
    public void clearCell(Position position, Map<Position, StackPane> cellMap) {
        StackPane cell = cellMap.get(position);
        if (cell != null) {
            cell.getChildren().clear();
        }
    }

    /**
     * Adds a victory animation to the cells corresponding to the given positions.
     *
     * @param pos the list of positions representing the cells where the victory animation should be applied
     * @param cellMap the map linking each position to its corresponding StackPane in the board grid
     */
    public void addVictoryAnimation(List<Position> pos, Map<Position, StackPane> cellMap) {
        GameAnimation.addVictoryAnimation(pos, cellMap);
    }
}