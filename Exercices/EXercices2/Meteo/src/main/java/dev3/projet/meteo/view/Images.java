package dev3.projet.meteo.view;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;



public class Images {


    public Image getImage(int temp) {
        String imagePath;
        if (temp == 0) {
            imagePath = "ciel_clair.png";
        } else if (temp >= 1 && temp <= 3) {
            imagePath = "nuageux.png";
        } else if (temp >= 45 && temp <= 48) {
            imagePath = "brouillard.png";
        } else if (temp >= 51 && temp <= 55) {
            imagePath = "rincer.png";
        } else if (temp >= 56 && temp <= 57) {
            imagePath = "gelgavage.png";
        } else if (temp >= 61 && temp <= 65) {
            imagePath = "pluie_leger.png";
        } else if (temp >= 66 && temp <= 67) {
            imagePath = "pluie_forte.png";
        } else if (temp >= 71 && temp <= 75) {
            imagePath = "neige_moderate.png";
        } else if (temp == 77) {
            imagePath = "neige.gif";
        } else if (temp >= 80 && temp <= 82) {
            imagePath = "douche_pluie.gif";
        } else if (temp >= 85 && temp <= 86) {
            imagePath = "douche_neige.gif";
        } else if (temp == 95) {
            imagePath = "orage_leger.gif";
        } else if (temp >= 96 && temp <= 99) {
            imagePath = "orage-lourde.gif";
        } else {
            imagePath = "soleil.gif";
        }

        return Objects.requireNonNull(showImage(imagePath)).getImage();

    }

    private ImageView showImage(String imagePath) {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + imagePath)));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);

            return imageView;

        } catch (Exception e) {
            showAlert( "Erreur lors de l'affichage de l'image.");
        }
        return null;
    }


        public void showAlert(String title) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'affichage de l'image.");
            alert.showAndWait();
        }

        private ImageView buttonSearch() {
            Image searchIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/recherche.gif")));
            ImageView searchIconView = new ImageView(searchIcon);
            searchIconView.setFitWidth(60);
            searchIconView.setFitHeight(20);

            return searchIconView;
        }

}
