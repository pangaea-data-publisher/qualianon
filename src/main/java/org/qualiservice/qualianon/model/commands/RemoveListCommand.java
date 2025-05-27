package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.categories.CategoryListScheme;
import org.qualiservice.qualianon.model.categories.CategoryScheme;


public class RemoveListCommand extends Command {

    private final CategoryScheme categoryScheme;
    private final CategoryListScheme oldList;


    public RemoveListCommand(CategoryScheme categoryScheme) {
        this.categoryScheme = categoryScheme;
        oldList = categoryScheme.getCategoryList();
    }

    @Override
    public String getDescription() {
        return "Remove list " + oldList.getFile();
    }

    @Override
    public boolean run() {
        categoryScheme.setCategoryList(null);
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        categoryScheme.setCategoryList(oldList);
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "RemoveListCommand{" +
                "project=" + project +
                ", categoryScheme=" + categoryScheme +
                '}';
    }
}
