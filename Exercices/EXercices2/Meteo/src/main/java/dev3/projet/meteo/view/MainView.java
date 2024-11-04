package dev3.projet.meteo.view;

import dev3.projet.meteo.controller.Controller;
import dev3.projet.meteo.model.WeatherObject;
import dev3.projet.meteo.model.observer.Observer;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDate;

public class MainView implements Observer {

    private Controller controller;
    private final InputView inputView;
    private final WeatherView weatherView;
    public Images images;

    private final Label cityLabel;
    private final Label tempMinLabel;
    private final Label tempMaxLabel;
    private final ImageView weatherImage;
    private final Button searchButton;
    private final ProgressIndicator progressIndicator;

    public MainView(Stage stage) {
        inputView = new InputView();
        weatherView = new WeatherView();
        this.images = new Images();
        searchButton = new Button("Search");

        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px;");

        Background background = new Background(new BackgroundFill(
                new javafx.scene.paint.LinearGradient(
                        0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web("#e0f7fa")),
                        new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web("#4fc3f7"))
                ),
                CornerRadii.EMPTY,
                Insets.EMPTY
        ));

        BorderPane root = new BorderPane();
        root.setBackground(background);

        HBox inputBox = new HBox(10, inputView.getText(), inputView.getDate(), searchButton);
        inputBox.setAlignment(Pos.CENTER);

        inputView.getText().setMaxWidth(Double.MAX_VALUE);
        inputView.getDate().setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(inputView.getText(), Priority.ALWAYS);
        HBox.setHgrow(inputView.getDate(), Priority.NEVER);
        HBox.setHgrow(searchButton, Priority.NEVER);

        root.setBottom(inputBox);

        cityLabel = new Label("City");
        cityLabel.setStyle("-fx-font-size: 38px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        tempMinLabel = new Label("12.4°");
        tempMinLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");

        tempMaxLabel = new Label("16.5°");
        tempMaxLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: #f44336; -fx-font-weight: bold;");

        weatherImage = new ImageView();

        VBox tempBx = new VBox(10, tempMaxLabel, tempMinLabel);
        tempBx.setAlignment(Pos.CENTER);

        HBox tempAndImageBox = new HBox(20);
        tempAndImageBox.setAlignment(Pos.CENTER);
        weatherImage.setFitWidth(220);
        weatherImage.setFitHeight(160);
        weatherImage.setPreserveRatio(true);
        tempAndImageBox.getChildren().addAll(tempBx, weatherImage);

        VBox weatherBox = new VBox(10);
        weatherBox.setAlignment(Pos.CENTER);
        weatherBox.getChildren().addAll(tempAndImageBox, cityLabel);
        root.setCenter(weatherBox);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(80, 80);
        progressIndicator.setStyle("-fx-progress-color: #4caf50;");

        VBox progressBox = new VBox(progressIndicator);
        progressBox.setAlignment(Pos.CENTER);
        root.setTop(progressBox);

        inputView.getText().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                performSearch();
            } else if (event.getCode() == KeyCode.RIGHT) {
                inputView.getDate().requestFocus();
            } else if (event.getCode() == KeyCode.DOWN) {
                searchButton.requestFocus();
            }
        });

        inputView.getDate().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                performSearch();
            } else if (event.getCode() == KeyCode.LEFT) {
                inputView.getText().requestFocus();
            } else if (event.getCode() == KeyCode.DOWN) {
                searchButton.requestFocus();
            }
        });

        searchButton.setOnAction(e -> performSearch());

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Executes the search for weather data based on user input.
     * It validates the inputs and displays a loading indicator during the fetch operation.
     */
    private void performSearch() {
        String city = inputView.getText().getText();
        LocalDate date = inputView.getDate().getValue();

        if (city == null || city.isEmpty()) {
            images.showAlert("City cannot be empty");
            return;
        }
        if (date == null) {
            images.showAlert("Please select a valid date");
            return;
        }

        Platform.runLater(() -> {
            cityLabel.setVisible(false);
            tempMinLabel.setVisible(false);
            tempMaxLabel.setVisible(false);
            weatherImage.setVisible(false);
            progressIndicator.setVisible(true);
        });

        Task<Void> fetchWeatherTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                controller.actionFetch(city, date);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    cityLabel.setVisible(true);
                    tempMinLabel.setVisible(true);
                    tempMaxLabel.setVisible(true);
                    weatherImage.setVisible(true);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    images.showAlert("Failed to fetch weather data. Please try again.");
                });
            }
        };

        new Thread(fetchWeatherTask).start();
    }

    /**
     * Sets the controller for this view.
     *
     * @param controller The controller to be set
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Updates the weather information displayed in the view.
     *
     * @param values The weather data to display
     */
    @Override
    public void update(WeatherObject values) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), weatherImage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            weatherView.setName(values.locality());
            weatherView.setTempMin(values.tempMin());
            weatherView.setTempMax(values.tempMax());
            weatherView.setWeathercode(values.weatherCode());

            cityLabel.setText(weatherView.getName());
            tempMinLabel.setText(weatherView.getTempMin() + "°");
            tempMaxLabel.setText(weatherView.getTempMax() + "°");
            weatherImage.setImage(images.getImage(weatherView.getWcode()));

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), weatherImage);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();

        FadeTransition fadeOutText = new FadeTransition(Duration.millis(500), tempMinLabel);
        fadeOutText.setFromValue(1.0);
        fadeOutText.setToValue(0.0);

        fadeOutText.setOnFinished(event -> {
            tempMinLabel.setText(values.tempMin() + "°");
            tempMaxLabel.setText(values.tempMax() + "°");

            FadeTransition fadeInText = new FadeTransition(Duration.millis(500), tempMinLabel);
            fadeInText.setFromValue(0.0);
            fadeInText.setToValue(1.0);
            fadeInText.play();
        });

        fadeOutText.play();
    }
}
