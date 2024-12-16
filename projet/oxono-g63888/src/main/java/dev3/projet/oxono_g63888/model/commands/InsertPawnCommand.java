package dev3.projet.oxono_g63888.model.commands;

import dev3.projet.oxono_g63888.model.*;

public class InsertPawnCommand implements Command {

    private Pawn pawn;
    private Position pawnPosition;

    /**
     * Creates a command to insert a pawn at the specified position.
     *
     * @param pawn The pawn to be inserted.
     * @param pawnPosition The position where the pawn will be placed on the board.
     */
    public InsertPawnCommand(Pawn pawn, Position pawnPosition) {
        this.pawn = pawn;
        this.pawnPosition = pawnPosition;
    }

    /**
     * Executes the command to insert a pawn at the specified position.
     * This method is part of the Command design pattern and implements
     * the operation defined for the InsertPawnCommand class.
     */
    @Override
    public void execute() {}

    /**
     * Reverts the effects of a previously executed command.
     *
     * The unexecute method is responsible for undoing the operation performed by
     * the execute method. It reverses changes in state or behavior introduced by
     * the execution of the command. Specific behavior of this method depends on
     * the implementation in the concrete class.
     */
    @Override
    public void unexecute() {}

    /**
     * Retrieves the current position of the pawn associated with this command.
     *
     * @return the position of the pawn as a {@code Position} object.
     */
    public Position getPawnPosition() {
        return pawnPosition;
    }

    /**
     * Retrieves the pawn associated with this command.
     *
     * @return the pawn contained in this command.
     */
    public Pawn getPawn() {
        return pawn;
    }
}

