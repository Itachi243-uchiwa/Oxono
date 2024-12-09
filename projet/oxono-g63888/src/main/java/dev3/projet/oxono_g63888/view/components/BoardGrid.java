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
        grid.setHgap(0);
        grid.setVgap(0);
        grid.getStyleClass().add("board-container");
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public GridPane getRoot() {
        return grid;
    }

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

    public void updatePawn(Position position, Pawn pawn, Map<Position, StackPane> cellMap) {
        clearColor("hover-move", cellMap);
        StackPane cell = cellMap.get(position);

        TokenGraphic pawnFx = createPawnFX(pawn);
        GameAnimation.addPawnDropAnimation(pawnFx);

        cell.getChildren().setAll(pawnFx);
        cellMap.put(position, cell);
    }

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

    private TokenGraphic createPawnFX(Pawn pawn) {
        Color color = pawn.getColor() == ColorPawn.PINK ? Color.DEEPPINK : Color.BLACK;
        TokenGraphic pawnFX = new TokenGraphic(color, TokenGraphic.PieceType.PAWN);
        pawnFX.setMark(pawn.getMark());
        return pawnFX;
    }

    private TokenGraphic createTotemFX(Totem totem) {
        TokenGraphic totemFX = new TokenGraphic(Color.CYAN, TokenGraphic.PieceType.TOTEM);
        totemFX.setMark(totem.getMark());
        return totemFX;
    }

    private StackPane createBoardCell() {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("board-cell");
        return cell;
    }

    public void clearColor(String color, Map<Position, StackPane> cellMap) {
        cellMap.values().forEach(cell ->
                cell.getStyleClass().removeIf(style -> style.equals(color))
        );
    }

    public void highlightPossibleMoves(List<Position> positions, String color, Map<Position, StackPane> cellMap) {
        clearColor(color, cellMap);
        positions.forEach(pos -> {
            StackPane cell = cellMap.get(pos);
            if (cell != null) {
                cell.getStyleClass().add(color);
            }
        });
    }

    public void clearCell(Position position, Map<Position, StackPane> cellMap) {
        StackPane cell = cellMap.get(position);
        if (cell != null) {
            cell.getChildren().clear();
        }
    }

    public void addVictoryAnimation(List<Position> pos, Map<Position, StackPane> cellMap) {
        GameAnimation.addVictoryAnimation(pos, cellMap);
    }
}