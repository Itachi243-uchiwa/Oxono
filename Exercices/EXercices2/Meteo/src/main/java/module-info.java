module dev3.projet.meteo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.net.http; // Si tu utilises HttpClient
    requires com.fasterxml.jackson.databind;

    opens dev3.projet.meteo to javafx.fxml; // Pour l'injection de dépendances FXML
    exports dev3.projet.meteo; // Pour l'accès à ce module
}
