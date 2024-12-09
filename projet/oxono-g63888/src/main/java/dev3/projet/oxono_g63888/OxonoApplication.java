package dev3.projet.oxono_g63888;

import dev3.projet.oxono_g63888.controller.GameController;
import dev3.projet.oxono_g63888.model.Game;
import dev3.projet.oxono_g63888.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class OxonoApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        Game game = new Game();
        GameView gameView = new GameView();
        GameController controller = new GameController(game, gameView);

        primaryStage.setTitle("Oxono Game");
        primaryStage.setScene(gameView.getScene());
        primaryStage.show();

        controller.showStartDialog();
    }

    public static void main(String[] args) {
        launch(args);
    }
}