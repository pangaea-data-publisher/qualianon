package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.properties.StringProperty;

import java.util.function.Consumer;


public class RenameCommand extends Command {

    private final String newName;
    private final StringProperty stringProperty;
    private final String oldName;
    private final Consumer<String> afterCallback;

    public RenameCommand(String newName, StringProperty stringProperty, Consumer<String> afterCallback) {
        this.newName = newName;
        this.stringProperty = stringProperty;
        oldName = stringProperty.getValue();
        this.afterCallback = afterCallback;
    }

    @Override
    public String getDescription() {
        return "Rename " + oldName;
    }

    @Override
    public boolean run() {
        stringProperty.setValue(newName);
        if (afterCallback != null) {
            afterCallback.accept(newName);
        }
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        stringProperty.setValue(oldName);
        if (afterCallback != null) {
            afterCallback.accept(oldName);
        }
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "RenameCommand{" +
                "project=" + project +
                ", newName='" + newName + '\'' +
                ", stringProperty=" + stringProperty +
                ", oldName='" + oldName + '\'' +
                '}';
    }
}
