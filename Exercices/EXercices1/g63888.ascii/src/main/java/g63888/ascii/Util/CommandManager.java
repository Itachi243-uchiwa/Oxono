package g63888.ascii.Util;

import java.util.Stack;

public class CommandManager {

        private Stack<Command> undoStack = new Stack<>();
        private Stack<Command> redoStack = new Stack<>();

        public void do_(Command command) {
            command.execute();
            undoStack.push(command);
            redoStack.clear();
        }

        public void undo() {
            if (!undoStack.isEmpty()) {
                Command command = undoStack.pop();
                command.unexecute();
                redoStack.push(command);
            }
        }

        public void redo() {
            if (!redoStack.isEmpty()) {
                Command command = redoStack.pop();
                command.execute();
                undoStack.push(command);
            }
        }
    }

