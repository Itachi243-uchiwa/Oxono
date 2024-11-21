package dev3.projet.oxono_g63888.model.commands;


import dev3.projet.oxono_g63888.model.*;

public class MoveTotemCommand implements Command {

    private Board board;
    private Totem totem;
    private Position newPosition;
    private Position oldPosition;

    public MoveTotemCommand(Board board, Totem totem, Position newPosition, Position oldPosition) throws OxonoException {
        this.board = board;
        this.totem = totem;
        this.newPosition = newPosition;

        this.oldPosition = oldPosition;
        if (oldPosition == null) {
            throw new OxonoException("Totem's initial position not found on the board.");
        }
    }

    /**
     * Executes the command by moving the totem to the specified new position.
     */
    @Override
    public void execute() throws OxonoException {
        board.moveTotem(totem, newPosition);
    }

    /**
     * Reverts the move by moving the totem back to its original position.
     */
    @Override
    public void unexecute() throws OxonoException {
        board.moveTotem(totem, oldPosition);
    }

    @Override
    public Mark getMovedMark() {
        return totem.getMark();
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public Position getOldPosition() {
        return oldPosition;
    }

    public Totem getTotem() {
        return totem;
    }
}
