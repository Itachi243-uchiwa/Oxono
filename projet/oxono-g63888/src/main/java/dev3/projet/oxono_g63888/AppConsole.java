package dev3.projet.oxono_g63888;

import dev3.projet.oxono_g63888.controller.ConsoleController;
import dev3.projet.oxono_g63888.model.Game;
import dev3.projet.oxono_g63888.view.ConsoleView;

public class AppConsole {
    public static void main(String[] args) {
        Game game = new Game();
        ConsoleView view = new ConsoleView();
        ConsoleController controller = new ConsoleController(game, view);
        controller.startGame();
    }
}
