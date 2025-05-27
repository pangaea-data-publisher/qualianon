package org.qualiservice.qualianon.model.commands;

import javafx.scene.paint.Color;
import org.qualiservice.qualianon.utility.ColorProperty;


public class SetColorPropertyCommand extends Command {

    private final ColorProperty colorProperty;
    private final Color color;
    private String label;
    private Color previousColor;

    public SetColorPropertyCommand(ColorProperty colorProperty, Color color, String label) {
        this.colorProperty = colorProperty;
        this.color = color;
        this.label = label;
    }

    @Override
    public String getDescription() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Set color ");
        sb.append(color.toString());
        sb.append(" ");
        sb.append(label);
        return sb.toString();
    }

    @Override
    public boolean run() {
        previousColor = colorProperty.getValue();
        colorProperty.setValue(color);
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        colorProperty.setValue(previousColor);
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "SetColorPropertyCommand{" +
                "project=" + project +
                ", colorProperty=" + colorProperty +
                ", color=" + color +
                ", label='" + label + '\'' +
                ", previousColor=" + previousColor +
                '}';
    }
}
