package dev3.projet.oxono_g63888.model.commands;

import dev3.projet.oxono_g63888.model.Mark;

public interface Command {
    void execute();

    void unexecute();
}
