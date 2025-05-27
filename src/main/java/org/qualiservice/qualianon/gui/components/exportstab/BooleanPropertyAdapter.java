package org.qualiservice.qualianon.gui.components.exportstab;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.properties.BooleanProperty;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.commands.SetBooleanPropertyCommand;
import org.qualiservice.qualianon.utility.StringUtils;


public class BooleanPropertyAdapter implements ItemAdapter {

    private final String label;
    private final CommandRunner commandRunner;
    private final CheckBox checkBox;

    public BooleanPropertyAdapter(BooleanProperty booleanProperty, String label, CommandRunner commandRunner) {
        this.label = label;
        this.commandRunner = commandRunner;

        checkBox = new CheckBox();
        checkBox.setOnAction(actionEvent -> {
            final SetBooleanPropertyCommand command = new SetBooleanPropertyCommand(checkBox.isSelected(), booleanProperty, this.label);
            this.commandRunner.runCommand(command);
        });
        update(booleanProperty);

        booleanProperty.addUpdateListener((boolean isDirect) -> update(booleanProperty));
    }

    private void update(BooleanProperty booleanProperty) {
        checkBox.setSelected(booleanProperty.isValue());
        checkBox.setText(StringUtils.textWithIndicator(label, booleanProperty.isModified()));
    }

    @Override
    public Node getNode() {
        return checkBox;
    }

}
