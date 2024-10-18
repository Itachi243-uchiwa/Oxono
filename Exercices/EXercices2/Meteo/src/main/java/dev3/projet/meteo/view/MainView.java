package dev3.projet.meteo.view;

import dev3.projet.meteo.controller.Controller;
import dev3.projet.meteo.model.WeatherObject;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;

/**
 * MainView class is the main UI component for displaying weather information.
 * It handles user input for city and date, fetches weather data, and displays the result.
 */
public class MainView {

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

    /**
     * Constructor to initialize the main view, setting up the layout and components.
     *
     * @param stage the primary stage where the scene is displayed
     */
    public MainView(Stage stage) {
        inputView = new InputView();
        weatherView = new WeatherView();
        this.images = new Images();
        searchButton = new Button("Search");

        stage.setTitle("Weather Application");
        BorderPane root = new BorderPane();

        HBox inputBox = new HBox(10, inputView.getText(), inputView.getDate(), searchButton);
        inputBox.setAlignment(Pos.CENTER);

        inputView.getText().setMaxWidth(Double.MAX_VALUE);
        inputView.getDate().setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(inputView.getText(), Priority.ALWAYS);
        HBox.setHgrow(inputView.getDate(), Priority.NEVER);
        HBox.setHgrow(searchButton, Priority.NEVER);

        root.setBottom(inputBox);

        cityLabel = new Label("Torgny");
        cityLabel.setStyle("-fx-font-size: 38px; -fx-font-weight: bold;");

        tempMinLabel = new Label("12.4°");
        tempMinLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: blue; -fx-font-weight: bold;");

        tempMaxLabel = new Label("16.5°");
        tempMaxLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: black; -fx-font-weight: bold;");

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
        progressIndicator.setVisible(false);

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

        searchButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                inputView.getDate().requestFocus();
            }
        });

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles the search operation for fetching weather data based on the city and date.
     * Displays the progress indicator and hides all other elements until the search is complete.
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
     * Sets the controller for the MainView to handle data fetching.
     *
     * @param controller the controller to be set
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Updates the weather view with new weather data retrieved from the model.
     *
     * @param values the WeatherObject containing the updated weather data
     */
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
