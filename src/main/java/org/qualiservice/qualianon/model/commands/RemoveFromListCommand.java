package org.qualiservice.qualianon.model.commands;

import java.util.List;


public class RemoveFromListCommand<E> extends Command {

    protected final int index;
    protected E element;
    protected List<E> list;


    public RemoveFromListCommand(E element, List<E> list) {
        this.element = element;
        this.list = list;
        index = list.indexOf(element);
    }

    @Override
    public String getDescription() {
        return "Remove element";
    }

    @Override
    public boolean run() {
        list.remove(element);
        return true;
    }

    @Override
    public boolean undo() {
        list.add(index, element);
        return true;
    }

}
