package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.utility.ListProperty;
import org.qualiservice.qualianon.utility.Updatable;

public class AddToListCommand<E extends Updatable> extends Command {

    private E element;
    private ListProperty<E> listProperty;

    public AddToListCommand(E element, ListProperty<E> listProperty) {
        this.element = element;
        this.listProperty = listProperty;
    }

    @Override
    public String getDescription() {
        return "Add element to list";
    }

    @Override
    public boolean run() {
        listProperty.add(element);
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        listProperty.remove(element);
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "AddToListCommand{" +
                "element=" + element +
                ", listProperty=" + listProperty +
                ", project=" + project +
                '}';
    }
}
