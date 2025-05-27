package org.qualiservice.qualianon.gui.components.categoriestab;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.model.commands.AddToListCommand;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.utility.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


public class LabelsFolderAdapter implements ItemAdapter {

    private final Label label;
    private final ContextMenu contextMenu;


    public LabelsFolderAdapter(CategoryScheme categoryScheme, CommandRunner commandRunner) {
        label = new Label("Labels");
        final MenuItem addLabelMenu = new MenuItem("Add label");
        addLabelMenu.setOnAction(actionEvent -> {
            final List<String> names = categoryScheme.getLabels().stream()
                    .map(LabelScheme::getName)
                    .collect(Collectors.toList());
            final String newLabel = StringUtils.getUniqueName(names, "New label");
            final AddToListCommand<LabelScheme> command = new AddToListCommand<>(new LabelScheme(newLabel), categoryScheme.getLabelsProperty());
            commandRunner.runCommand(command);
        });
        contextMenu = new ContextMenu(addLabelMenu);
    }

    @Override
    public Node getNode() {
        return label;
    }

    @Override
    public ContextMenu getContextMenu() {
        return contextMenu;
    }

}
