package dev3.projet.oxono_g63888.model.commands;

import dev3.projet.oxono_g63888.model.OxonoException;

import java.util.Stack;

public class CommandManager {

    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();

    /**
     * Executes the provided command and updates the undo/redo stacks to reflect the new state.
     * This method clears the redo stack after executing a new command to maintain a consistent history.
     *
     * @param command The command to be executed. It implements the {@code Command} interface and must define
     *                the logic for execution and unexecution. The command is also pushed to the undo stack
     *                for potential reversal.
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Reverts the last executed command by unexecuting it and moves it to
     * the redo stack for potential redoing.
     *
     * @return The command that was undone.
     * @throws OxonoException if no commands are available to undo.
     */
    public Command undo() {
        if (!canUndo()) {
            throw new OxonoException("Can't undo");
        }
        Command command = undoStack.pop();
        command.unexecute();
        redoStack.push(command);
        return command;
    }

    /**
     * Redoes the most recently undone command by re-executing it and adding it back to the undo stack.
     * The command is removed from the redo stack as part of the operation.
     *
     * @return The command that was redone.
     * @throws OxonoException If no redo operation is possible.
     */
    public Command redo() {
        if (!canRedo()) {
            throw new OxonoException("Can't redo");
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        return command;
    }

    public Command getUndoPeek() {
        return undoStack.peek();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}

