package org.qualiservice.qualianon.gui.components.categoriestab;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.tools.trees.MyTreeCell;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.project.Project;

import java.util.List;
import java.util.stream.Collectors;


public class CategoriesTreeComponent extends AnchorPane {

    private final Project project;
    private final UIInterface uiInterface;
    private final TreeView<ItemAdapter> treeView;
    private final TreeItem<ItemAdapter> rootItem;


    public CategoriesTreeComponent(Project project, UIInterface uiInterface) {
        this.project = project;
        this.uiInterface = uiInterface;

        rootItem = new TreeItem<>();
        treeView = new TreeView<>();
        treeView.setCellFactory(view -> new MyTreeCell());
        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);
        treeView.setEditable(true);
        treeView.getSelectionModel().selectedItemProperty().addListener((observableValue, treeItemMultipleSelectionModel, treeItem) -> {
            if (treeItem == null) return;
            if (treeItem.getValue().getEditMenuItem() == null) {
                uiInterface.getRenameMenuItem().clearContext();
            } else {
                uiInterface.getRenameMenuItem().setContext(() -> {
                    uiInterface.showCategoriesTab();
                    treeView.edit(treeItem);
                });
            }
        });
        getChildren().add(treeView);

        project.getCategories().getCategoriesProperty().addUpdateListener((boolean isDirect) -> {
            if (!isDirect) return;
            fillView();
        });
        fillView();

        GuiUtility.extend(treeView);
        GuiUtility.extend(this);
    }

    private void fillView() {
        final List<TreeItem<ItemAdapter>> items = project.getCategories().getCategories().stream()
                .map(categoryScheme -> {
                    final TreeItem<ItemAdapter> treeItem = new TreeItem<>(new CategoryAdapter(categoryScheme, project, uiInterface));
                    treeItem.getChildren().add(makeLabelsItem(categoryScheme));
                    treeItem.getChildren().add(makeColorItem(categoryScheme));
                    treeItem.getChildren().add(makeFilelistItem(categoryScheme));
                    return treeItem;
                })
                .collect(Collectors.toList());

        rootItem.getChildren().retainAll(items);
        rootItem.getChildren().setAll(items);
    }

    private TreeItem<ItemAdapter> makeColorItem(CategoryScheme categoryScheme) {
        return new TreeItem<>(new ColorAdapter(categoryScheme.getColorProperty(), project));
    }

    private TreeItem<ItemAdapter> makeFilelistItem(CategoryScheme categoryScheme) {
        return new TreeItem<>(new ListfileAdapter(project.getCategories(), categoryScheme, uiInterface, project.getCategoriesDirectory(), project));
    }

    private TreeItem<ItemAdapter> makeLabelsItem(CategoryScheme categoryScheme) {
        final LabelsFolderAdapter labelsFolderAdapter = new LabelsFolderAdapter(categoryScheme, project);
        final TreeItem<ItemAdapter> treeItem = new TreeItem<>(labelsFolderAdapter);
        fillLabels(categoryScheme, treeItem);
        categoryScheme.getLabelsProperty().addUpdateListener((boolean isDirect) -> {
            if (!isDirect) return;
            treeItem.getChildren().clear();
            fillLabels(categoryScheme, treeItem);
        });
        return treeItem;
    }

    private void fillLabels(CategoryScheme categoryScheme, TreeItem<ItemAdapter> treeItem) {
        categoryScheme.getLabelsProperty().forEach(labelScheme -> {
            final LabelAdapter labelAdapter = new LabelAdapter(labelScheme, categoryScheme, project.getReplacementCollection(), project, uiInterface);
            final TreeItem<ItemAdapter> labelTreeItem = new TreeItem<>(labelAdapter);
            fillChoices(labelScheme, labelTreeItem);
            labelScheme.getChoicesProperty().addUpdateListener((boolean isDirect) -> {
                if (!isDirect) return;
                labelTreeItem.getChildren().clear();
                fillChoices(labelScheme, labelTreeItem);
            });
            treeItem.getChildren().add(labelTreeItem);
        });
    }

    private void fillChoices(org.qualiservice.qualianon.model.categories.LabelScheme labelScheme, TreeItem<ItemAdapter> labelTreeItem) {
        labelScheme.getChoicesProperty().forEach(choice -> {
            labelTreeItem.getChildren().add(new TreeItem<>(new ChoiceAdapter(choice, labelScheme, project)));
        });
    }

}
