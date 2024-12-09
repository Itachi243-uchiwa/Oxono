package dev3.projet.oxono_g63888.model.commands;

import dev3.projet.oxono_g63888.model.*;

public class InsertPawnCommand implements Command {

    private Pawn pawn;
    private Position pawnPosition;

    public InsertPawnCommand(Pawn pawn, Position pawnPosition) {
        this.pawn = pawn;
        this.pawnPosition = pawnPosition;
    }

    @Override
    public void execute() {}

    @Override
    public void unexecute() {}

    @Override
    public Mark getMovedMark() {
        return null;
    }

    public Position getPawnPosition() {
        return pawnPosition;
    }

    public Pawn getPawn() {
        return pawn;
    }
}

