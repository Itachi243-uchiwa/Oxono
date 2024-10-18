package dev3.projet.meteo.view;


public class WeatherView {

     private String name;
     private double tempMin;
     private double tempMax;
     private int Wcode;

    public WeatherView() {
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }
    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }
    public void setWeathercode(int Wcode) {
        this.Wcode = Wcode;
    }

    public String getName() {
        return name;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public int getWcode() {
        return Wcode;
    }
}

