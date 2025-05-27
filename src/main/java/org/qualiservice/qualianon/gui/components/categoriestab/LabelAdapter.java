package org.qualiservice.qualianon.gui.components.categoriestab;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.tools.MenuHelper;
import org.qualiservice.qualianon.gui.tools.StandardDialogs;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.model.commands.AddToListCommand;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.commands.RemoveLabelCommand;
import org.qualiservice.qualianon.model.commands.RenameCommand;
import org.qualiservice.qualianon.model.project.ReplacementCollection;
import org.qualiservice.qualianon.model.properties.StringProperty;
import org.qualiservice.qualianon.utility.ListProperty;

import java.util.Locale;
import java.util.Optional;


public class LabelAdapter implements ItemAdapter {

    private final HBox hBox;
    private final Label label;
    private final LabelScheme labelScheme;
    private final CategoryScheme categoryScheme;
    private final ReplacementCollection replacementCollection;
    private final CommandRunner commandRunner;
    private final UIInterface uiInterface;
    private final MenuItem renameMenu;
    private final TextField textField;
    private final ContextMenu contextMenu;


    public LabelAdapter(LabelScheme labelScheme, CategoryScheme categoryScheme, ReplacementCollection replacementCollection, CommandRunner commandRunner, UIInterface uiInterface) {
        this.labelScheme = labelScheme;
        this.categoryScheme = categoryScheme;
        this.replacementCollection = replacementCollection;
        this.commandRunner = commandRunner;
        this.uiInterface = uiInterface;
        label = new Label();
        hBox = new HBox(label);
        update();
        labelScheme.getNameProperty().addUpdateListener((boolean isDirect) -> update());
        labelScheme.getChoicesProperty().addUpdateListener((boolean isDirect) -> update());
        final MenuItem addChoiceMenu = new MenuItem("Add choice");
        addChoiceMenu.setOnAction(actionEvent -> runAddCommand());
        renameMenu = new MenuItem("Rename");
        final ListProperty<LabelScheme> labelsProperty = categoryScheme.getLabelsProperty();

        final MenuItem deleteMenu = new MenuItem("Delete");
        deleteMenu.setOnAction(actionEvent -> {
            final boolean preconditionOk = checkDeletePrecondition();
            if (!preconditionOk) return;
            final RemoveLabelCommand command = new RemoveLabelCommand(replacementCollection, categoryScheme, labelScheme);
            commandRunner.runCommand(command);
        });

        final MenuItem moveUpMenu = MenuHelper.getMoveUpMenuItem(labelScheme, labelsProperty, commandRunner);
        final MenuItem moveDownMenu = MenuHelper.getMoveDownMenuItem(labelScheme, labelsProperty, commandRunner);
        contextMenu = new ContextMenu(addChoiceMenu, renameMenu, deleteMenu, moveUpMenu, moveDownMenu);
        textField = new TextField();
    }

    private boolean checkDeletePrecondition() {
        final long usages = replacementCollection
                .getForCategory(categoryScheme).stream()
                .map(replacement -> replacement.isLabelUsed(labelScheme.getName()))
                .filter(isUsed -> isUsed)
                .count();
        if (usages == 0) return true;

        return StandardDialogs.userConfirmation(
                "Label \"" + labelScheme.getName() + "\" is used in " + usages + " replacement(s).\nThe label will be removed from these replacements. Ok?",
                uiInterface.getStage()
        );
    }

    private void runAddCommand() {
        final AddToListCommand<StringProperty> command = new AddToListCommand<>(new StringProperty("New choice"), labelScheme.getChoicesProperty());
        commandRunner.runCommand(command);
    }

    private void update() {
        if (labelScheme.hasChoices()) {
            label.setText(labelScheme.getName() + " (choice)");
        } else {
            label.setText(labelScheme.getName() + " (text)");
        }
    }

    @Override
    public Node getNode() {
        return hBox;
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
        textField.setText(labelScheme.getName());
        return textField;
    }

    @Override
    public void commitEdit() {
        final String newName = textField.getText();
        if (newName.equals(labelScheme.getName())) return;

        final Optional<LabelScheme> optionalLabelScheme = categoryScheme.getLabels().stream()
                .filter(l -> l.getName().toLowerCase(Locale.ROOT).equals(newName.toLowerCase(Locale.ROOT)))
                .filter(l -> l.hashCode() != labelScheme.hashCode())
                .findAny();

        if (optionalLabelScheme.isPresent()) {
            StandardDialogs.errorAlert("Label names must be unique within a category", uiInterface.getStage());
            return;
        }

        final RenameCommand command = new RenameCommand(textField.getText(), labelScheme.getNameProperty(), null);
        commandRunner.runCommand(command);
    }

}
