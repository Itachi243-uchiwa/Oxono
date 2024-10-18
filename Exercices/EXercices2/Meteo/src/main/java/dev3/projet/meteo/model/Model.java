package dev3.projet.meteo.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Model {

    private String address;
    private LocalDate date;
    private Map<String, WeatherObject> cache;

    public Model(String address, LocalDate date) {
        this.address = address;
        this.date = date;
        this.cache = new HashMap<>();
    }

    public WeatherObject fetch(String address, LocalDate date) throws WeatherException {
        String key = address + "_" + date.toString();

        if (cache.containsKey(key)) {
            System.out.println("Fetching weather object from cache");
            return cache.get(key);
        }

        String datum = String.valueOf(date);
        WeatherObject weatherData = WeatherApi.fetch(address, datum);
        cache.put(key, weatherData);

        return weatherData;
    }
}
