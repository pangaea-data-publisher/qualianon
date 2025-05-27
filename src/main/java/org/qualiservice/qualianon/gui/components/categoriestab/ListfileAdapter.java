package org.qualiservice.qualianon.gui.components.categoriestab;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.components.listimport.Importer;
import org.qualiservice.qualianon.gui.components.listimport.ListImportController;
import org.qualiservice.qualianon.gui.components.listlookup.ListLookupWindow;
import org.qualiservice.qualianon.gui.tools.StandardDialogs;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.categories.Categories;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.CodingList;
import org.qualiservice.qualianon.model.categories.SelectionStyle;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.commands.ImportListCommand;
import org.qualiservice.qualianon.model.commands.RemoveListCommand;
import org.qualiservice.qualianon.model.commands.SetListStyleCommand;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class ListfileAdapter implements ItemAdapter {

    private final VBox vBox;
    private final Label filenameLabel;
    private final Button loadButton;
    private final Button removeButton;
    private final Button viewButton;
    private final ComboBox<String> listStyleCombo;
    private final File categoriesDirectory;
    private final CommandRunner commandRunner;
    private final CategoryScheme categoryScheme;
    private final UIInterface uiInterface;
    private final ChangeListener<String> styleChangeListener;


    public ListfileAdapter(Categories categories, CategoryScheme categoryScheme, UIInterface uiInterface, File categoriesDirectory, CommandRunner commandRunner) {
        this.categoryScheme = categoryScheme;
        this.uiInterface = uiInterface;
        this.categoriesDirectory = categoriesDirectory;
        this.commandRunner = commandRunner;
        filenameLabel = new Label();
        loadButton = new Button("Load");
        loadButton.setOnAction(actionEvent -> {
            try {
                final ListImportController controller = ListImportController.show(uiInterface);
                if (controller.isCancelled()) return;
                importList(controller.getFile(), controller.getImporter());

            } catch (IOException e) {
                uiInterface.getMessageLogger().logError("List import error", e);
            }
        });
        removeButton = new Button("Remove");
        removeButton.setOnAction(actionEvent -> {
            if (!StandardDialogs.userConfirmation("Would you like to remove the list from the category?\nExisting markers and replacements will not be changed and you can later load another list.", uiInterface.getStage())) {
                return;
            }
            final RemoveListCommand command = new RemoveListCommand(this.categoryScheme);
            commandRunner.runCommand(command);
        });
        final HBox hBox = new HBox(
                new Label("List lookup:"),
                filenameLabel,
                loadButton,
                removeButton
        );
        hBox.setAlignment(Pos.BASELINE_LEFT);
        hBox.setSpacing(7f);

        listStyleCombo = new ComboBox<>();
        listStyleCombo.getItems().add("List");
        listStyleCombo.getItems().add("Tree");
        listStyleCombo.getSelectionModel().select(1);
        styleChangeListener = (observableValue, s, t1) -> {
            final SelectionStyle selectionStyle = SelectionStyle.valueOf(t1.toUpperCase());
            final SetListStyleCommand command = new SetListStyleCommand(categoryScheme, selectionStyle);
            commandRunner.runCommand(command);
        };
        listStyleCombo.getSelectionModel().selectedItemProperty().addListener(styleChangeListener);

        viewButton = new Button("View");
        viewButton.setOnAction(actionEvent ->
                ListLookupWindow.openWindow(
                        categoryScheme,
                        categories.getCodingList(categoryScheme),
                        "",
                        uiInterface.getStage(),
                        uiInterface.getMessageLogger()
                )
        );

        final HBox hBox2 = new HBox(new Label("   "), loadButton, removeButton, new Label("Style:"), listStyleCombo, viewButton);
        hBox2.setAlignment(Pos.BASELINE_LEFT);
        hBox2.setSpacing(7f);

        vBox = new VBox(hBox, hBox2);
        vBox.setSpacing(4f);
        update();
        this.categoryScheme.addUpdateListener((boolean isDirect) -> update());
    }

    private void update() {
        loadButton.setDisable(categoryScheme.hasCategoryList());
        listStyleCombo.setDisable(!categoryScheme.hasCategoryList());
        removeButton.setDisable(!categoryScheme.hasCategoryList());
        viewButton.setDisable(!categoryScheme.hasCategoryList());
        if (categoryScheme.hasCategoryList()) {
            filenameLabel.setText(categoryScheme.getCategoryList().getFile());
            listStyleCombo.getSelectionModel().selectedItemProperty().removeListener(styleChangeListener);
            listStyleCombo.getSelectionModel().select(categoryScheme.getCategoryList().getStyle().ordinal());
            listStyleCombo.getSelectionModel().selectedItemProperty().addListener(styleChangeListener);
        } else {
            filenameLabel.setText("<none>");
        }
    }

    private void importList(File file, Importer importer) {

        final CodingList codingList = importer.importFile(file, uiInterface.getMessageLogger());
        if (codingList == null) return;

        final List<String> parameters = codingList.getHeader();
        final String listLabels = parameters.stream().reduce((s, s2) -> s + "\n" + s2).orElse("(none)");

        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Import Lookup List");
        alert.setHeaderText("Import and create missing labels?");
        alert.setContentText("The following labels are defined in the file:\n" + listLabels);
        alert.initOwner(uiInterface.getStage());
        final Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() != ButtonType.OK) return;

        final ImportListCommand command = new ImportListCommand(categoryScheme, codingList, categoriesDirectory, file, importer, uiInterface);
        commandRunner.runCommand(command);
    }

    @Override
    public Node getNode() {
        return vBox;
    }

}
