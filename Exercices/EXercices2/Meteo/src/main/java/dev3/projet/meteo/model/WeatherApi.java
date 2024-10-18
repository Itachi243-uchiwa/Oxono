package dev3.projet.meteo.model;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class WeatherApi {


    private static final HttpClient client = HttpClient.newHttpClient();


    static WeatherObject fetch(String city, String date) throws WeatherException {
        try {

            JsonNode geoData = getJsonCity(city);

            double latitude = geoData.get(0).get("lat").asDouble();
            double longitude = geoData.get(0).get("lon").asDouble();
            String cityName = geoData.get(0).get("display_name").asText();

            String firstCityName = cityName.split(" ")[0];

            JsonNode weatherData = getApiMeteo(latitude, longitude, date);

            JsonNode dailyData = weatherData.get("daily");
            if (dailyData == null) {
                throw new WeatherException("No weather data available for the given date.");
            }

            double tempMax = dailyData.get("temperature_2m_max").get(0).asDouble();
            double tempMin = dailyData.get("temperature_2m_min").get(0).asDouble();
            int weatherCode = dailyData.get("weathercode").get(0).asInt();


            return new WeatherObject(firstCityName, date, weatherCode, tempMin, tempMax);

        } catch (Exception e) {
            throw new WeatherException("Failed to fetch weather data", e);
        }
    }


    private static JsonNode getJsonCity(String city) throws Exception {



        String geoUrl = "https://nominatim.openstreetmap.org/search.php?q=" + city + "&format=jsonv2";
        HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(geoUrl)).build();
        HttpResponse<String> geoResponse = client.send(geoRequest, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(geoResponse.body());

    }


    private static JsonNode getApiMeteo(double latitude, double longitude, String date) throws Exception {
        String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude
                + "&daily=temperature_2m_max,temperature_2m_min,weathercode&timezone=Europe%2FBerlin&start_date=" + date + "&end_date=" + date;
        HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).build();

        HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(weatherResponse.body());
    }
}

