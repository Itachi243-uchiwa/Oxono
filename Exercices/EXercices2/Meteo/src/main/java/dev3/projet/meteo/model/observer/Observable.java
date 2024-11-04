package dev3.projet.meteo.model.observer;

import dev3.projet.meteo.model.WeatherObject;

public interface Observable {
    public void registerObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void notifyObservers(WeatherObject object);
}
