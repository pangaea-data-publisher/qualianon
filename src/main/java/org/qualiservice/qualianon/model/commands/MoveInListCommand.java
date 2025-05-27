package org.qualiservice.qualianon.model.commands;

import java.util.List;


public class MoveInListCommand<E> extends Command {

    private final E element;
    private final List<E> list;
    private int diff;
    private final int index;

    public MoveInListCommand(E element, List<E> list, int diff) {
        this.element = element;
        this.list = list;
        this.diff = diff;
        index = list.indexOf(element);
    }

    @Override
    public String getDescription() {
        if(diff<0) {
            return "Move up";
        } else {
            return "Move down";
        }
    }

    @Override
    public boolean run() {
        list.remove(element);
        list.add(index + diff, element);
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        list.remove(element);
        list.add(index, element);
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "MoveInListCommand{" +
                "project=" + project +
                ", element=" + element +
                ", diff=" + diff +
                ", index=" + index +
                '}';
    }
}
