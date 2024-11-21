package dev3.projet.oxono_g63888.model.Observer;

public interface Observable {

    void registerObserver(Observer o);

    void removeObserver(Observer o);

    void notifyObservers(OxonoEvent event);
}
