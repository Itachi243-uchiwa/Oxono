package dev3.projet.oxono_g63888.model.commands;

import dev3.projet.oxono_g63888.model.OxonoException;

import java.util.Stack;

public class CommandManager {

    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();

    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public Command undo() {
        if (!canUndo()) {
            throw new OxonoException("Can't undo");
        }
        Command command = undoStack.pop();
        command.unexecute();
        redoStack.push(command);
        return command;
    }

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

