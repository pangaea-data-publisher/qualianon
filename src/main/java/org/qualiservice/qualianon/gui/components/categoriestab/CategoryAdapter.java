package org.qualiservice.qualianon.gui.components.categoriestab;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.tools.MenuHelper;
import org.qualiservice.qualianon.gui.tools.StandardDialogs;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.categories.Categories;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.commands.RemoveCategoryCommand;
import org.qualiservice.qualianon.model.commands.RenameCommand;
import org.qualiservice.qualianon.model.project.Project;
import org.qualiservice.qualianon.model.project.ReplacementCollection;

import java.util.Locale;
import java.util.Optional;


public class CategoryAdapter implements ItemAdapter {

    private final Label label;
    private final CategoryScheme categoryScheme;
    private final Categories categories;
    private final ReplacementCollection replacementCollection;
    private final CommandRunner commandRunner;
    private final UIInterface uiInterface;
    private final MenuItem rename;
    private final TextField textField;
    private final ContextMenu contextMenu;
    private final Project project;


    public CategoryAdapter(CategoryScheme categoryScheme, Project project, UIInterface uiInterface) {
        this.project = project;
        this.categoryScheme = categoryScheme;
        this.categories = project.getCategories();
        this.replacementCollection = project.getReplacementCollection();
        this.commandRunner = project;
        this.uiInterface = uiInterface;
        label = new Label();
        update();
        categoryScheme.getNameProperty().addUpdateListener((boolean isDirect) -> update());
        rename = new MenuItem("Rename");

        final MenuItem deleteMenu = new MenuItem("Delete");
        deleteMenu.setOnAction(actionEvent -> {
            final boolean preconditionOk = checkDeletePrecondition(categoryScheme);
            if (!preconditionOk) return;
            final RemoveCategoryCommand command = new RemoveCategoryCommand(categoryScheme, categories.getCategoriesProperty(), project);
            commandRunner.runCommand(command);
        });

        final MenuItem moveUpMenu = MenuHelper.getMoveUpMenuItem(categoryScheme, categories.getCategoriesProperty(), commandRunner);
        final MenuItem moveDownMenu = MenuHelper.getMoveDownMenuItem(categoryScheme, categories.getCategoriesProperty(), commandRunner);
        contextMenu = new ContextMenu(rename, deleteMenu, moveUpMenu, moveDownMenu);
        textField = new TextField();
    }

    private boolean checkDeletePrecondition(CategoryScheme categoryScheme) {
        final long replacementCount = replacementCollection.getForCategory(categoryScheme).size();
        final int markerCount = replacementCollection.getForCategory(categoryScheme).stream()
                .map(project::countOccurrences)
                .reduce(Integer::sum)
                .orElse(0);
        if (replacementCount == 0 && markerCount == 0) return true;

        return StandardDialogs.userConfirmation(
                "Category \"" + categoryScheme.getName() + "\" is used in " + replacementCount + " replacement(s) and " + markerCount + " marker(s).\nRelated replacements and markers will be removed. Ok?",
                uiInterface.getStage()
        );
    }

    private void update() {
        label.setText(categoryScheme.getName());
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
        textField.setText(categoryScheme.getName());
        return textField;
    }

    @Override
    public void commitEdit() {
        final String newName = CategoryScheme.makeSafeName(textField.getText());
        if (newName.equals(categoryScheme.getName())) return;

        final Optional<CategoryScheme> categorySchemeOptional = categories.getCategories().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(newName.toLowerCase(Locale.ROOT)))
                .filter(c -> c.hashCode() != categoryScheme.hashCode())
                .findAny();

        if (categorySchemeOptional.isPresent()) {
            StandardDialogs.errorAlert("Category names must be unique", uiInterface.getStage());
            return;
        }

        final RenameCommand command = new RenameCommand(newName, categoryScheme.getNameProperty(), null);
        commandRunner.runCommand(command);
    }

}
