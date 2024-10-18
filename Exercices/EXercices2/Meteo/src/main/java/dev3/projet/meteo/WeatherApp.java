package dev3.projet.meteo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherApp extends Application {

    private TextField cityField;
    private DatePicker datePicker;
    private Button actionButton;
    private Label resultLabel;
    private VBox weatherAnimationContainer;
    private HttpClient client = HttpClient.newHttpClient();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather Application");
        BorderPane root = new BorderPane();

        cityField = new TextField();
        cityField.setPromptText("City");
        cityField.setMaxWidth(Double.MAX_VALUE);

        datePicker = new DatePicker();
        Image searchIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/recherche.gif")));
        ImageView searchIconView = new ImageView(searchIcon);
        searchIconView.setFitWidth(60);
        searchIconView.setFitHeight(20);
        actionButton = new Button();
        actionButton.setGraphic(searchIconView);

        resultLabel = new Label("Weather info will be displayed here");
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        weatherAnimationContainer = new VBox();
        weatherAnimationContainer.setAlignment(Pos.CENTER);

        VBox weatherInfoLayout = new VBox(10, weatherAnimationContainer);
        weatherInfoLayout.setAlignment(Pos.CENTER);
        root.setCenter(weatherInfoLayout);

        HBox inputLayout = new HBox(10, cityField, datePicker, actionButton);
        inputLayout.setAlignment(Pos.CENTER);
        root.setBottom(inputLayout);

        actionButton.setOnAction(e -> getWeather());

        Scene scene = new Scene(root, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void getWeather() {
        String city = cityField.getText();

        if (city.isEmpty() || datePicker.getValue() == null) {
            showAlert("Error", "Please enter a city and select a date.");
            return;
        }

        String date = datePicker.getValue().toString();

        try {
            JsonNode geoData = getJsonCity(city);
            if (geoData.isEmpty()) {
                showAlert("Error", "City not found.");
                return;
            }

            double latitude = geoData.get(0).get("lat").asDouble();
            double longitude = geoData.get(0).get("lon").asDouble();
            String cityName = geoData.get(0).get("display_name").asText();

            String firstCityName = cityName.split(" ")[0];

            JsonNode dailyData = getApiMeteo(latitude, longitude, date).get("daily");
            if (dailyData == null) {
                showAlert("Error", "No weather data available for this date.");
                return;
            }

            double tempMax = dailyData.get("temperature_2m_max").get(0).asDouble();
            double tempMin = dailyData.get("temperature_2m_min").get(0).asDouble();
            int weatherCode = dailyData.get("weathercode").get(0).asInt();

            weatherAnimationContainer.getChildren().clear();

            Label cityLabel = new Label("City: " + firstCityName);
            cityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            Label tempMinLabel = new Label("Min Temp: " + tempMin + "°C");
            tempMinLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            Label tempMaxLabel = new Label("Max Temp: " + tempMax + "°C");
            tempMaxLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            weatherAnimationContainer.getChildren().addAll(cityLabel, tempMinLabel, tempMaxLabel);

            getImage(weatherCode);

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "An error occurred while fetching the weather data.");
        }
    }

    private JsonNode getJsonCity(String city) throws Exception {
        String geoUrl = "https://nominatim.openstreetmap.org/search.php?q=" + city + "&format=jsonv2";
        HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(geoUrl)).build();
        HttpResponse<String> geoResponse = client.send(geoRequest, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(geoResponse.body());
    }

    private JsonNode getApiMeteo(double latitude, double longitude, String date) throws Exception {
        String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude
                + "&daily=temperature_2m_max,temperature_2m_min,weathercode&timezone=Europe%2FBerlin&start_date=" + date + "&end_date=" + date;
        HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).build();

        HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(weatherResponse.body());
    }

    public void getImage(int temp) {
        String imagePath;
        if (temp == 0) {
            imagePath = "ciel_clair.png";
        } else if (temp >= 1 && temp <= 3) {
            imagePath = "nuageux.png";
        } else if (temp >= 45 && temp <= 48) {
            imagePath = "brouillard.png";
        } else if (temp >= 51 && temp <= 55) {
            imagePath = "rincer.png";
        } else if (temp >= 56 && temp <= 57) {
            imagePath = "gelgavage.png";
        } else if (temp >= 61 && temp <= 65) {
            imagePath = "pluie_leger.png";
        } else if (temp >= 66 && temp <= 67) {
            imagePath = "pluie_forte.png";
        } else if (temp >= 71 && temp <= 75) {
            imagePath = "neige_moderate.png";
        } else if (temp == 77) {
            imagePath = "neige.gif";
        } else if (temp >= 80 && temp <= 82) {
            imagePath = "douche_pluie.gif";
        } else if (temp >= 85 && temp <= 86) {
            imagePath = "douche_neige.gif";
        } else if (temp == 95) {
            imagePath = "orage_leger.gif";
        } else if (temp >= 96 && temp <= 99) {
            imagePath = "orage-lourde.gif";
        } else {
            imagePath = "soleil.gif";
        }

        showImage(imagePath, 200);
        System.out.println(temp);
    }

    private void showImage(String imagePath, int width) {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + imagePath)));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setPreserveRatio(true);

            weatherAnimationContainer.getChildren().add(imageView);

        } catch (Exception e) {
            showAlert(String.valueOf(Alert.AlertType.ERROR), "Erreur lors de l'affichage de l'image.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
