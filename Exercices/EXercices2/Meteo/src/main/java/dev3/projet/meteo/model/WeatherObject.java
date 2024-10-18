package dev3.projet.meteo.model;

public record WeatherObject(String locality, String date, int weatherCode, double tempMin, double tempMax) {}
