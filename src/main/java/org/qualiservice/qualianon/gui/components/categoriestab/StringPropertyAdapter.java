package org.qualiservice.qualianon.gui.components.categoriestab;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.commands.RenameCommand;
import org.qualiservice.qualianon.model.properties.StringProperty;


public class StringPropertyAdapter implements ItemAdapter {

    private final Label label;
    private final ContextMenu contextMenu;
    private final MenuItem rename;
    private final TextField textField;
    private final StringProperty stringProperty;
    private CommandRunner commandRunner;


    public StringPropertyAdapter(StringProperty stringProperty, boolean editable, CommandRunner commandRunner) {
        this.stringProperty = stringProperty;
        this.commandRunner = commandRunner;
        label = new Label();
        update(stringProperty);
        stringProperty.addUpdateListener((boolean isDirect) -> update(stringProperty));

        if (editable) {
            rename = new MenuItem("Rename");
            contextMenu = new ContextMenu(rename);
            textField = new TextField();
        } else {
            rename = null;
            contextMenu = null;
            textField = null;
        }
    }

    private void update(StringProperty choice) {
        label.setText(choice.getValue());
    }

    @Override
    public Node getNode() {
        return label;
    }

    @Override
    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    @Override
    public MenuItem getEditMenuItem() {
        return rename;
    }

    @Override
    public TextField getEditableTextField() {
        textField.setText(label.getText());
        return textField;
    }

    @Override
    public void commitEdit() {
        final RenameCommand command = new RenameCommand(textField.getText(), stringProperty, null);
        commandRunner.runCommand(command);
    }

}
