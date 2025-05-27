package org.qualiservice.qualianon.gui.components.exportstab;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.gui.tools.trees.MyTreeCell;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.anonymization.CategoryProfile;
import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Project;

import java.util.LinkedList;
import java.util.List;


public class ExportsTreeComponent extends AnchorPane {

    private final TreeView<ItemAdapter> treeView;
    private final Project project;
    private UIInterface uiInterface;
    private final List<AnonymizedDocumentSelectionListener> selectionListeners;
    private TreeItem<ItemAdapter> rootItem;


    public ExportsTreeComponent(Project project, UIInterface uiInterface) {
        this.project = project;
        this.uiInterface = uiInterface;
        this.selectionListeners = new LinkedList<>();

        treeView = new TreeView<>();
        treeView.setCellFactory(view -> new MyTreeCell());
        getChildren().add(treeView);

        fillView();
        project.getExportList().addUpdateListener((boolean isDirect) -> fillView());

        GuiUtility.extend(treeView);
        GuiUtility.extend(this);
    }

    private void fillView() {
        rootItem = new TreeItem<>(new StringAdapter("Exports"));

        project.getExportList().getExports().forEach(export -> {
            final TreeItem<ItemAdapter> exportItem = new TreeItem<>(new ExportAdapter(export, project.getExportList(), project.getAnonymizedFiles(), uiInterface, project));
            exportItem.getChildren().add(makeAnonymizationSettingsItem(export.getAnonymizationProfile()));
            exportItem.getChildren().add(makeDocumentsItem(export, project.getAnonymizedFiles()));
            rootItem.getChildren().add(exportItem);
        });

        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);
    }

    private TreeItem<ItemAdapter> makeAnonymizationSettingsItem(AnonymizationProfile anonymizationProfile) {
        final TreeItem<ItemAdapter> item = new TreeItem<>(new StringAdapter("Anonymization Settings"));
        anonymizationProfile.getCategoryProfiles().forEach(categoryProfile -> {
            final TreeItem<ItemAdapter> categoryItem = makeCategoryTreeItem(categoryProfile);
            item.getChildren().add(categoryItem);
        });
        return item;
    }

    private TreeItem<ItemAdapter> makeDocumentsItem(Export export, ObservableList<AnonymizedFile> anonymizedFiles) {
        final TreeItem<ItemAdapter> item = new TreeItem<>(new StringAdapter("Export Documents"));
        anonymizedFiles.forEach(anonymizedFile -> {
            final TreeItem<ItemAdapter> documentItem = new TreeItem<>(new DocumentAdapter(anonymizedFile, export, selectionListeners));
            item.getChildren().add(documentItem);
        });
        return item;
    }

    private TreeItem<ItemAdapter> makeCategoryTreeItem(CategoryProfile categoryProfile) {
        final TreeItem<ItemAdapter> item = new TreeItem<>(new CategoryAdapter(categoryProfile));
        final TreeItem<ItemAdapter> originalAdapterTreeItem = new TreeItem<>(new BooleanPropertyAdapter(
                categoryProfile.getOriginalProperty(), "Original", project
        ));
        item.getChildren().add(originalAdapterTreeItem);
        final TreeItem<ItemAdapter> countAdapterTreeItem = new TreeItem<>(new BooleanPropertyAdapter(
                categoryProfile.getCountingProperty(), "Counting", project
        ));
        item.getChildren().add(countAdapterTreeItem);

        categoryProfile.getLabelProfiles().forEach(labelProfile -> {
            final BooleanPropertyAdapter labelAdapter = new BooleanPropertyAdapter(
                    labelProfile.getProperty(), labelProfile.getName(), project
            );
            item.getChildren().add(new TreeItem<>(labelAdapter));
        });

        return item;
    }

    public ExportsTreeComponent addSelectionListener(AnonymizedDocumentSelectionListener listener) {
        selectionListeners.add(listener);
        return this;
    }

    public void openExport(Export export) {
        rootItem.getChildren().forEach(item -> {
            final ItemAdapter itemAdapter = item.getValue();
            if (!(itemAdapter instanceof ExportAdapter)) return;
            final ExportAdapter exportAdapter = (ExportAdapter) itemAdapter;
            if (exportAdapter.getExport().equals(export)) {
                item.setExpanded(true);
                item.getChildren().forEach(subItem -> subItem.setExpanded(true));
            } else {
                item.setExpanded(false);
            }
        });
    }

    public interface AnonymizedDocumentSelectionListener {
        void onAnonymizedDocumentSelected(AnonymizedFile document, Export export);
    }

}
