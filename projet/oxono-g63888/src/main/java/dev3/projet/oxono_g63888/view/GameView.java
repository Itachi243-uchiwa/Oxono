package dev3.projet.oxono_g63888.view;

import dev3.projet.oxono_g63888.controller.GameController;
import dev3.projet.oxono_g63888.model.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameView {
    private GridPane boardGrid;
    private VBox root;
    private Label statusLabel;
    private Label playerTurnLabel;
    private Button undoButton;
    private Button redoButton;
    private Button surrenderButton;
    private VBox pinkPlayerRack;
    private VBox blackPlayerRack;
    private GameController controller;
    private Map<Position, StackPane> cellMap;
    private Button quitButton;
    private Button aiMove;

    public GameView() {
        cellMap = new HashMap<>();
        createView();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Crée la vue principale du jeu
     */
    private void createView() {
        root = new VBox(30);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        VBox gameContainer = new VBox(20);
        gameContainer.setAlignment(Pos.CENTER);

        HBox header = createHeader();
        boardGrid = createBoardGrid();
        HBox playerRacks = createPlayerRacks();

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        gameContainer.getChildren().addAll(header, boardGrid, playerRacks, statusLabel);
        root.getChildren().add(gameContainer);
    }

    /**
     * Crée l'en-tête du jeu avec les contrôles
     */
    private HBox createHeader() {
        HBox header = new HBox(25);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header");

        playerTurnLabel = new Label("Tour actuel: Joueur Rose");
        playerTurnLabel.getStyleClass().add("player-turn-label");

        undoButton = new Button("Annuler");
        redoButton = new Button("Refaire");
        surrenderButton = new Button("Abandonner");
        quitButton = new Button("Quitter");
        aiMove = new Button("IA");


        for (Button btn : Arrays.asList(undoButton, redoButton, surrenderButton, quitButton, aiMove)) {
            btn.getStyleClass().add("game-button");
        }

        header.getChildren().addAll(playerTurnLabel, undoButton, redoButton, surrenderButton, quitButton, aiMove);
        return header;
    }

    /**
     * Crée la grille du plateau de jeu
     */
    private GridPane createBoardGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(0);
        grid.setVgap(0);
        grid.getStyleClass().add("board-container");
        return grid;
    }

    /**
     * Crée les racks des joueurs
     */
    private HBox createPlayerRacks() {
        HBox racksContainer = new HBox(20);
        racksContainer.setAlignment(Pos.CENTER);
        racksContainer.getStyleClass().add("rack-container");

        pinkPlayerRack = createPlayerRackSection("Joueur Rose");

        Region separator = new Region();
        separator.getStyleClass().add("separator");
        separator.setPrefHeight(2);
        separator.setMaxWidth(Double.MAX_VALUE);

        blackPlayerRack = createPlayerRackSection("Joueur Noir");

        racksContainer.getChildren().addAll(pinkPlayerRack, separator, blackPlayerRack);
        return racksContainer;
    }

    /**
     * Crée une section de rack pour un joueur
     */
    private VBox createPlayerRackSection(String playerName) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(playerName);
        nameLabel.getStyleClass().add("rack-label");

        section.getChildren().add(nameLabel);
        return section;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    /**
     * Met à jour le plateau de jeu
     */
    public void updateBoard(Game game) {
        boardGrid.getChildren().clear();
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

                boardGrid.add(cell, j, i);
                cellMap.put(pos, cell);
            }
        }
    }

    public void updatePawn(Position position, Pawn pawn) {
        clearColor("hover-move");
        StackPane cell = cellMap.get(position);

        TokenGraphic pawnFx = createPawnFX(pawn);
        GameAnimation.addPawnDropAnimation(pawnFx);

        cell.getChildren().setAll(pawnFx);
        cellMap.put(position, cell);
    }

    public void updateTotem(Position position, Totem totem, Position oldpos) {
        clearColor("valid-move");

        StackPane oldCell = cellMap.get(oldpos);
        oldCell.getChildren().clear();

        StackPane newCell = cellMap.get(position);
        TokenGraphic totemFX = createTotemFX(totem);

        GameAnimation.addTotemBounceAnimation(totemFX);
        newCell.getChildren().setAll(totemFX);
        cellMap.put(oldpos, oldCell);
        cellMap.put(position, newCell);
    }


    public void clearColor(String color) {
        cellMap.values().forEach(cell ->
                cell.getStyleClass().removeIf(style -> style.equals(color))
        );
    }

    /**
     * Crée un jeton visuel pour un pion
     */
    public TokenGraphic createPawnFX(Pawn pawn) {
        Color color = pawn.getColor() == ColorPawn.PINK ? Color.DEEPPINK : Color.BLACK;
        TokenGraphic pawnFX = new TokenGraphic(color, TokenGraphic.PieceType.PAWN);
        pawnFX.setMark(pawn.getMark());
        return pawnFX;
    }

    /**
     * Crée un jeton visuel pour un totem
     */
    public TokenGraphic createTotemFX(Totem totem) {
        TokenGraphic totemFX = new TokenGraphic(Color.CYAN, TokenGraphic.PieceType.TOTEM);
        totemFX.setMark(totem.getMark());
        return totemFX;
    }

    /**
     * Met en surbrillance les mouvements possibles
     */
    public void highlightPossibleMoves(List<Position> positions, String color) {
        clearColor(color);
        positions.forEach(pos -> {
            StackPane cell = cellMap.get(pos);
            if (cell != null) {
                cell.getStyleClass().add(color);
            }
        });
    }

    /**
     * Crée une cellule du plateau
     */
    private StackPane createBoardCell() {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("board-cell");
        return cell;
    }

    /**
     * Met à jour les racks des joueurs
     */
    public void updateRacks(int[] remainingPawns) {
        updatePlayerRack(pinkPlayerRack, remainingPawns[0], remainingPawns[1], ColorPawn.PINK);
        updatePlayerRack(blackPlayerRack, remainingPawns[2], remainingPawns[3], ColorPawn.BLACK);
    }

    public void clearCell(Position position) {
        StackPane cell = cellMap.get(position);
        if (cell != null) {
            cell.getChildren().clear();  // Efface tout le contenu de la cellule
        }
    }

    /**
     * Met à jour le rack d'un joueur
     */
    private void updatePlayerRack(VBox rack, int xPawns, int oPawns, ColorPawn color) {
        // Garde le label du joueur
        Node playerLabel = rack.getChildren().get(0);
        rack.getChildren().clear();
        rack.getChildren().add(playerLabel);

        // Crée la ligne des pions X
        VBox xSection = new VBox(5);
        xSection.setAlignment(Pos.CENTER);
        Label xLabel = new Label("Pions X");
        xLabel.getStyleClass().add("rack-label");

        HBox xPawnsBox = new HBox(5);
        xPawnsBox.setAlignment(Pos.CENTER);
        xPawnsBox.getStyleClass().add("pawns-container");

        for (int i = 0; i < xPawns; i++) {
            Pawn pawn = new Pawn(color, Mark.X);
            TokenGraphic pawnFX = createPawnFX(pawn);
            xPawnsBox.getChildren().add(pawnFX);
        }
        xSection.getChildren().addAll(xLabel, xPawnsBox);

        // Crée la ligne des pions O
        VBox oSection = new VBox(5);
        oSection.setAlignment(Pos.CENTER);
        Label oLabel = new Label("Pions O");
        oLabel.getStyleClass().add("rack-label");

        HBox oPawnsBox = new HBox(5);
        oPawnsBox.setAlignment(Pos.CENTER);
        oPawnsBox.getStyleClass().add("pawns-container");

        for (int i = 0; i < oPawns; i++) {
            Pawn pawn = new Pawn(color, Mark.O);
            TokenGraphic pawnFX = createPawnFX(pawn);
            oPawnsBox.getChildren().add(pawnFX);
        }
        oSection.getChildren().addAll(oLabel, oPawnsBox);

        rack.getChildren().addAll(xSection, oSection);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    public VBox getRoot() {
        return root;
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

    public Button getAiMoveButton() {
        return aiMove;
    }

    public void setCurrentPlayer(String message) {
        playerTurnLabel.setText(message);
    }
    public void addVictoryAnimation(List<Position> pos){
         GameAnimation.addVictoryAnimation(pos, cellMap);
    }

}