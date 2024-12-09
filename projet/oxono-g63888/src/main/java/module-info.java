module your.module.name {
    // Dépendances nécessaires (ajoutez celles dont vous avez besoin)
    requires javafx.controls; // Exemple de module pour JavaFX (ajoutez selon les besoins)
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.naming;

    // Exportez le package contenant votre classe JavaFX
    exports dev3.projet.oxono_g63888.view to javafx.graphics;
    exports dev3.projet.oxono_g63888;
    exports dev3.projet.oxono_g63888.view.utils to javafx.graphics;
    exports dev3.projet.oxono_g63888.view.components to javafx.graphics; // Exporte le package principal


    // Ajoutez d'autres exports en fonction des besoins de votre projet
}
