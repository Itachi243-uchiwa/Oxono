package dev3.projet.oxono_g63888.view.components;

import dev3.projet.oxono_g63888.model.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class PlayerRacks {
    private HBox racksContainer;
    private VBox pinkPlayerRack;
    private VBox blackPlayerRack;

    public PlayerRacks() {
        createPlayerRacks();
    }

    private void createPlayerRacks() {
        racksContainer = new HBox(20);
        racksContainer.setAlignment(Pos.CENTER);
        racksContainer.getStyleClass().add("rack-container");

        pinkPlayerRack = createPlayerRackSection("PLayer Pink");

        Region separator = new Region();
        separator.getStyleClass().add("separator");
        separator.setPrefHeight(2);
        separator.setMaxWidth(Double.MAX_VALUE);

        blackPlayerRack = createPlayerRackSection("Player Black");

        racksContainer.getChildren().addAll(pinkPlayerRack, separator, blackPlayerRack);
    }

    private VBox createPlayerRackSection(String playerName) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(playerName);
        nameLabel.getStyleClass().add("rack-label");

        section.getChildren().add(nameLabel);
        return section;
    }

    public void updateRacks(int[] remainingPawns) {
        updatePlayerRack(pinkPlayerRack, remainingPawns[0], remainingPawns[1], ColorPawn.PINK);
        updatePlayerRack(blackPlayerRack, remainingPawns[2], remainingPawns[3], ColorPawn.BLACK);
    }

    private void updatePlayerRack(VBox rack, int xPawns, int oPawns, ColorPawn color) {
        Node playerLabel = rack.getChildren().get(0);
        rack.getChildren().clear();
        rack.getChildren().add(playerLabel);

        VBox xSection = createPawnSection( xPawns, color, Mark.X);
        VBox oSection = createPawnSection( oPawns, color, Mark.O);

        rack.getChildren().addAll(xSection, oSection);
    }

    private VBox createPawnSection(int count, ColorPawn color, Mark mark) {
        VBox section = new VBox(5);
        section.setAlignment(Pos.CENTER);


        HBox pawnsBox = new HBox(5);
        pawnsBox.setAlignment(Pos.CENTER);
        pawnsBox.getStyleClass().add("pawns-container");

        for (int i = 0; i < count; i++) {
            Pawn pawn = new Pawn(color, mark);
            TokenGraphic pawnFX = new TokenGraphic(
                color == ColorPawn.PINK ? Color.DEEPPINK : Color.BLACK,
                TokenGraphic.PieceType.PAWN
            );
            pawnFX.setMark(mark);
            pawnsBox.getChildren().add(pawnFX);
        }

        section.getChildren().add(pawnsBox);
        return section;
    }

    public HBox getRoot() {
        return racksContainer;
    }
}