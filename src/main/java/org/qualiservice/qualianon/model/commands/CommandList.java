package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.audit.MessageLogger;

import java.util.LinkedList;
import java.util.NoSuchElementException;


public class CommandList {

    private final LinkedList<Command> commands;
    private final LinkedList<CommandListListener> listeners;
    private int pointer;
    private final MessageLogger logger;

    public CommandList(MessageLogger logger) {
        this.logger = logger;
        commands = new LinkedList<>();
        listeners = new LinkedList<>();
    }

    public void addListener(CommandListListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CommandListListener listener) {
        listeners.remove(listener);
    }

    /**
     * Executes a command and adds it to the undo stack if successful.
     */
    public void runCommand(Command command) {
        try {
            final boolean success = command.run();
            if (!success) {
                logger.logError(command.getDescription(), null);
                return;
            }
        } catch (Exception e) {
            logger.logError(command.getDescription(), e);
            return;
        }

        while (commands.size() > pointer) {
            commands.removeLast();
        }
        commands.add(command);
        pointer++;
        notifyListeners();
    }

    /**
     * Undoes the last executed command and updates listeners.
     */
    public void undo() {
        if (pointer == 0) throw new NoSuchElementException();
        pointer--;
        final boolean success = commands.get(pointer).undo();
        if (!success) {
            clearCommandList();
        }
        notifyListeners();
    }

    /**
     * Redoes the next command in the history, if available.
     */
    public void redo() {
        if (pointer >= commands.size()) throw new NoSuchElementException();
        final boolean success = commands.get(pointer).run();
        pointer++;
        if (!success) {
            clearCommandList();
        }
        notifyListeners();
    }

    /**
     * Clears command history on unrecoverable errors.
     */
    private void clearCommandList() {
        commands.clear();
        pointer = 0;
    }

    /**
     * Notifies listeners about current undo/redo availability.
     */
    private void notifyListeners() {
        listeners.forEach(listener -> {
            boolean undoEnabled = false;
            String undoDescription = "";
            boolean redoEnabled = false;
            String redoDescription = "";

            if (pointer > 0) {
                undoEnabled = true;
                undoDescription = commands.get(pointer - 1).getDescription();
            }
            if (pointer < commands.size()) {
                redoEnabled = true;
                redoDescription = commands.get(pointer).getDescription();
            }

            listener.onUndoRedoChanged(undoEnabled, undoDescription, redoEnabled, redoDescription);
        });
    }

    public interface CommandListListener {
        void onUndoRedoChanged(boolean undoEnabled, String undoDescription, boolean redoEnabled, String redoDescription);
    }
}
