package dev3.projet.meteo;

import dev3.projet.meteo.controller.Controller;
import dev3.projet.meteo.model.Model;
import dev3.projet.meteo.view.MainView;
import javafx.application.Application;
import javafx.stage.Stage;
import java.time.LocalDate;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        Model model = new Model();

        MainView mainView = new MainView(primaryStage);

        Controller controller = new Controller(model, mainView);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
