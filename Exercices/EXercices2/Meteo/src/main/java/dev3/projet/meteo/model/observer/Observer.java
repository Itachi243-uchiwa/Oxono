package dev3.projet.meteo.model.observer;

import dev3.projet.meteo.model.WeatherObject;

public interface Observer {
    void update(WeatherObject weatherObject);
}
