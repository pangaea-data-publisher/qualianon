package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.properties.BooleanProperty;


public class SetBooleanPropertyCommand extends Command {

    private final boolean enabled;
    private final BooleanProperty booleanProperty;
    private final String label;
    private boolean previousValue;

    public SetBooleanPropertyCommand(boolean enabled, BooleanProperty booleanProperty, String label) {
        this.enabled = enabled;
        this.booleanProperty = booleanProperty;
        this.label = label;
    }

    @Override
    public String getDescription() {
        final StringBuilder sb = new StringBuilder();
        if (enabled) sb.append("Enable ");
        else sb.append("Disable ");
        sb.append(label);
        return sb.toString();
    }

    @Override
    public boolean run() {
        previousValue = booleanProperty.isValue();
        booleanProperty.setValue(enabled);
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        booleanProperty.setValue(previousValue);
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "SetBooleanPropertyCommand{" +
                "project=" + project +
                ", enabled=" + enabled +
                ", booleanProperty=" + booleanProperty +
                ", label='" + label + '\'' +
                ", previousValue=" + previousValue +
                '}';
    }
}
