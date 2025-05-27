package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.project.Project;

import java.util.List;
import java.util.stream.Collectors;


public class RemoveCategoryCommand extends RemoveFromListCommand<CategoryScheme> {

    private final List<ReplacementDeleter> replacementDeleters;

    public RemoveCategoryCommand(CategoryScheme element, List<CategoryScheme> list, Project project) {
        super(element, list);
        replacementDeleters = project.getReplacementCollection().getForCategory(element).stream()
                .map(replacement -> new ReplacementDeleter(replacement, project.getReplacementCollection(), project.getAnonymizedFiles()))
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return "Remove category (incl. replacements and markers)";
    }

    @Override
    public boolean run() {
        replacementDeleters.forEach(ReplacementDeleter::run);
        super.run();
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        super.undo();
        replacementDeleters.forEach(ReplacementDeleter::undo);
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "RemoveCategoryCommand{" +
                "project=" + project +
                ", replacementDeleters=" + replacementDeleters +
                ", index=" + index +
                ", element=" + element +
                '}';
    }
}
