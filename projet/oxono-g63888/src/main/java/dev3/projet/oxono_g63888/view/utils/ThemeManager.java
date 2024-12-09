package dev3.projet.oxono_g63888.view.utils;
import javafx.scene.Scene;

public class ThemeManager {
    private Scene scene;
    private String currentTheme;

    public ThemeManager(Scene scene) {
        this.scene = scene;
    }

    public void setTheme(String themeName) {
        // Nettoyer les styles précédents
        scene.getStylesheets().clear();

        // Appliquer le nouveau thème
        String cssPath = switch (themeName.toLowerCase()) {
            case "uchiwa" -> "/styles/uchiwa-theme.css";
            case "futuristic" -> "/styles/futuristic-theme.css";
            case "colorblind" -> "/styles/colorblind-theme.css";
            case "luxury" -> "/styles/luxury-theme.css";
            case "dark" -> "/styles/dark-theme.css";
            case "light" -> "/styles/light-theme.css";
            case "retro" -> "/styles/retro-theme.css";
            case "vintage" -> "/styles/vintage-theme.css";
            case "cartoon" -> "/styles/cartoon-theme.css";
            case "christmas" -> "/styles/christmas-theme.css";
            case "cyberpunk" -> "/styles/cyberpunk-theme.css";
            case "space" -> "/styles/space-theme.css";
            case "football" -> "/styles/football-theme.css";
            case "jungle" -> "/styles/jungle-theme.css";
            case "naruto vs sasuke" -> "/styles/naruto-vs-sasuke.css";
            default -> "/styles/default-theme.css";
        };

        scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        currentTheme = themeName;
    }

    public String getCurrentTheme() {
        return currentTheme;
    }
}