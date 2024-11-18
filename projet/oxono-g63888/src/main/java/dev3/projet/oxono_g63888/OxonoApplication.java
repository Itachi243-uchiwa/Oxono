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

        Scene scene = new Scene(gameView.getRoot(), 1000, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

        primaryStage.setTitle("Oxono Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        controller.showStartDialog();
    }

    public static void main(String[] args) {
        launch(args);
    }
}