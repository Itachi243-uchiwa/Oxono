package dev3.projet.oxono_g63888.model.commands;

import dev3.projet.oxono_g63888.model.*;

public class InsertPawnCommand implements Command {

    private Board board;
    private Pawn pawn;
    private Player player;
    private Position totemPosition;
    private Position pawnPosition;

    public InsertPawnCommand(Board board, Pawn pawn, Player player, Position totemPosition, Position pawnPosition) {
        this.board = board;
        this.pawn = pawn;
        this.player = player;
        this.totemPosition = totemPosition;
        this.pawnPosition = pawnPosition;
    }

    @Override
    public void execute() {
        if (player.hasPawn(pawn.getMark())) {
            player.usePawn(pawn.getMark());
        }
    }

    @Override
    public void unexecute() {
        player.returnPawn(pawn.getMark());
    }

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

