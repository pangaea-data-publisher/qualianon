package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.SelectionStyle;


public class SetListStyleCommand extends Command {

    private final CategoryScheme categoryScheme;
    private final SelectionStyle selectionStyle;
    private final SelectionStyle prevSelectionStyle;


    public SetListStyleCommand(CategoryScheme categoryScheme, SelectionStyle selectionStyle) {
        this.categoryScheme = categoryScheme;
        this.selectionStyle = selectionStyle;
        this.prevSelectionStyle = categoryScheme.getCategoryList().getStyle();
    }

    @Override
    public String getDescription() {
        return "Set list style " + selectionStyle;
    }

    @Override
    public boolean run() {
        categoryScheme.getCategoryList().setStyle(selectionStyle);
        categoryScheme.setCategoryList(categoryScheme.getCategoryList()); // To trigger update mechanism
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        categoryScheme.getCategoryList().setStyle(prevSelectionStyle);
        categoryScheme.setCategoryList(categoryScheme.getCategoryList()); // To trigger update mechanism
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "SetListStyleCommand{" +
                "project=" + project +
                ", categoryScheme=" + categoryScheme +
                ", selectionStyle=" + selectionStyle +
                ", prevSelectionStyle=" + prevSelectionStyle +
                '}';
    }
}
