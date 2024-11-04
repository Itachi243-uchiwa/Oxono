package dev3.projet.meteo.model;

import dev3.projet.meteo.model.observer.Observable;
import dev3.projet.meteo.model.observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model implements Observable {

    private Map<String, WeatherObject> cache;
    private List<Observer> observers = new ArrayList<Observer>();

    public Model() {
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

        notifyObservers(weatherData);

        return weatherData;
    }

        @Override
        public void registerObserver(Observer observer) {
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        }

        @Override
        public void removeObserver(Observer observer) {
            observers.remove(observer);
        }

        @Override
        public void notifyObservers(WeatherObject object) {
            for (Observer observer : observers) {
                observer.update(object);
            }
        }
    }

