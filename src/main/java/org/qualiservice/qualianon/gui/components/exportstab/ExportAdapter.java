package org.qualiservice.qualianon.gui.components.exportstab;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.tools.StandardDialogs;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.commands.*;
import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.exports.ExportList;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.utility.StringUtils;

import java.io.IOException;
import java.util.List;


class ExportAdapter implements ItemAdapter {

    private final Label label;
    private final ContextMenu contextMenu;
    private Export export;

    public ExportAdapter(Export export, ExportList exportList, List<AnonymizedFile> anonymizedFiles, UIInterface uiInterface, CommandRunner commandRunner) {
        this.export = export;

        label = new Label();
        update(export);

        final MenuItem save = new MenuItem("Save settings & documents");
        save.setOnAction(actionEvent -> {
            actionEvent.consume();
            try {
                export.saveSettingsAndDocuments(anonymizedFiles);
            } catch (IOException ignored) {
            }
        });

        final MenuItem rename = new MenuItem("Rename export");
        rename.setOnAction(actionEvent -> {
            actionEvent.consume();
            StandardDialogs
                    .textInput("Rename export settings", export.getName(), uiInterface.getStage())
                    .ifPresent(name -> {
                        final Command command = new RenameExportCommand(name, export, exportList);
                        commandRunner.runCommand(command);
                    });
        });

        final MenuItem duplicate = new MenuItem("Duplicate export");
        duplicate.setOnAction(actionEvent -> {
            actionEvent.consume();
            final String defaultName = exportList.getUniqueName("Copy of " + export.getName());
            StandardDialogs
                    .textInput("Duplicate export settings", defaultName, uiInterface.getStage())
                    .ifPresent(name -> {
                        final Command command = new DuplicateExportCommand(name, export, exportList);
                        commandRunner.runCommand(command);
                    });
        });

        final MenuItem delete = new MenuItem("Delete export");
        delete.setOnAction(actionEvent -> {
            actionEvent.consume();
            if (!StandardDialogs.userConfirmation("Would you like to remove the export?", uiInterface.getStage())) {
                return;
            }
            final Command command = new DeleteExportCommand(export, exportList);
            commandRunner.runCommand(command);
        });

        contextMenu = new ContextMenu(save, rename, duplicate, delete);
    }

    @Override
    public Node getNode() {
        return label;
    }

    @Override
    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public Export getExport() {
        return export;
    }

    private void update(Export export) {
        label.setText(
                StringUtils.textWithIndicator(
                        export.getName(),
                        export.getAnonymizationProfile().isModified()
                )
        );
    }

}
