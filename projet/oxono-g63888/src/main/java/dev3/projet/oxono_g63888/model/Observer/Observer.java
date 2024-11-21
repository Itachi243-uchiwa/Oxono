package dev3.projet.oxono_g63888.model.Observer;

import dev3.projet.oxono_g63888.model.Game;

public interface Observer {
    void update(Game game, OxonoEvent event);
}
