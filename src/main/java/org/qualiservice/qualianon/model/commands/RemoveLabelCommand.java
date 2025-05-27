package org.qualiservice.qualianon.model.commands;

import javafx.util.Pair;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.model.project.Label;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;

import java.util.List;
import java.util.stream.Collectors;


public class RemoveLabelCommand extends Command {

    private final CategoryScheme categoryScheme;
    private final LabelScheme labelScheme;
    private final List<Pair<Replacement, Label>> labelsToRemove;
    private final int labelIndex;

    public RemoveLabelCommand(ReplacementCollection replacementCollection, CategoryScheme categoryScheme, LabelScheme labelScheme) {
        this.categoryScheme = categoryScheme;
        this.labelScheme = labelScheme;
        labelIndex = categoryScheme.getLabelsProperty().indexOf(labelScheme);
        labelsToRemove = replacementCollection.getForCategory(categoryScheme).stream()
                .filter(replacement -> replacement.isLabelUsed(labelScheme.getName()))
                .map(replacement -> new Pair<>(
                        replacement,
                        replacement.getLabel(labelScheme.getName())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return "Remove label " + labelScheme.getName() + " from category " + categoryScheme.getName();
    }

    @Override
    public boolean run() {
        categoryScheme.removeLabel(labelScheme);
        labelsToRemove.forEach(pair ->
                pair.getKey().removeLabel(pair.getValue())
        );
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        categoryScheme.getLabelsProperty().add(labelIndex, labelScheme);
        labelsToRemove.forEach(pair ->
                pair.getKey().addLabel(pair.getValue())
        );
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "RemoveLabelCommand{" +
                "project=" + project +
                ", categoryScheme=" + categoryScheme +
                ", labelScheme=" + labelScheme +
                ", labelsToRemove=" + labelsToRemove +
                ", labelIndex=" + labelIndex +
                '}';
    }
}
