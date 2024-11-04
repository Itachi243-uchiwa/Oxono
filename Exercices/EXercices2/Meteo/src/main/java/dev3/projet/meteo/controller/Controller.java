package dev3.projet.meteo.controller;


import dev3.projet.meteo.model.Model;
import dev3.projet.meteo.view.MainView;
import dev3.projet.meteo.model.WeatherObject;
import java.time.LocalDate;

public class Controller {

    private final Model model;
    private final MainView view;

    public Controller(Model model, MainView view) {
        this.model = model;
        this.view = view;
        model.registerObserver(view);
        view.setController(this);
    }

    public void actionFetch(String address, LocalDate date) {
        try {
            WeatherObject weatherData = model.fetch(address, date);
            model.notifyObservers(weatherData);
        } catch (Exception e) {
            view.images.showAlert("Error fetching weather data: " + e.getMessage());
        }
    }
}

