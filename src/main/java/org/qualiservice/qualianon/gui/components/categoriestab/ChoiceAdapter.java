package org.qualiservice.qualianon.gui.components.categoriestab;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.qualiservice.qualianon.gui.tools.MenuHelper;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.commands.RenameCommand;
import org.qualiservice.qualianon.model.properties.StringProperty;
import org.qualiservice.qualianon.utility.ListProperty;


public class ChoiceAdapter implements ItemAdapter {

    private final Label label;
    private final ContextMenu contextMenu;
    private final MenuItem renameMenu;
    private final MenuItem deleteMenu;
    private final MenuItem moveUpMenu;
    private final MenuItem moveDownMenu;
    private final TextField textField;
    private final StringProperty stringProperty;
    private CommandRunner commandRunner;


    public ChoiceAdapter(StringProperty choice, LabelScheme labelScheme, CommandRunner commandRunner) {
        this.stringProperty = choice;
        this.commandRunner = commandRunner;
        label = new Label();
        update(choice);
        choice.addUpdateListener((boolean isDirect) -> update(choice));

        renameMenu = new MenuItem("Rename");
        final ListProperty<StringProperty> choicesProperty = labelScheme.getChoicesProperty();
        deleteMenu = MenuHelper.getDeleteMenuItem(choice, choicesProperty, null, commandRunner);
        moveUpMenu = MenuHelper.getMoveUpMenuItem(choice, choicesProperty, commandRunner);
        moveDownMenu = MenuHelper.getMoveDownMenuItem(choice, choicesProperty, commandRunner);
        contextMenu = new ContextMenu(renameMenu, deleteMenu, moveUpMenu, moveDownMenu);
        textField = new TextField();
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
        return renameMenu;
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
