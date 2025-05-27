package org.qualiservice.qualianon.gui.components.markerstab;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.trees.CheckboxAdapter;
import org.qualiservice.qualianon.gui.tools.trees.FolderAdapter;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.text.Coords;
import org.qualiservice.qualianon.model.text.MarkerRuntime;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class MarkersTree extends AnchorPane implements ListChangeListener<AnonymizedFile> {

    private final List<MarkerSelectListener> listeners;
    private final TreeView<CheckboxAdapter> treeView;
    private final Replacement replacement;
    private final ObservableList<AnonymizedFile> documents;

    public MarkersTree(Replacement replacement, ObservableList<AnonymizedFile> documents) {
        this.replacement = replacement;
        this.documents = documents;
        listeners = new LinkedList<>();
        treeView = new TreeView<>();
        treeView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, number, t1) -> notifySelectionListeners(t1)
        );
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        getChildren().add(treeView);

        documents.addListener(this);
        populateTreeView();

        GuiUtility.extend(treeView);
    }

    public void close() {
        documents.removeListener(this);
    }

    @Override
    public void onChanged(Change change) {
        populateTreeView();
        doCountAndNotify();
    }

    private void populateTreeView() {
        final String rootText = replacement.getCategoryScheme().getName() + " | " + replacement.getLabelsTextLine();
        final CheckboxAdapter rootAdapter = new FolderAdapter(rootText);
        final CheckBoxTreeItem<CheckboxAdapter> rootItem = new CheckBoxTreeItem<>(rootAdapter);
        rootItem.setExpanded(true);
        rootItem.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), this::handleCheckBoxChange);
        treeView.setRoot(rootItem);

        for (final AnonymizedFile document : documents) {
            CheckBoxTreeItem<CheckboxAdapter> folderItem = null;

            for (final MarkerRuntime markerRuntime : document.getDocument().getMarkers()) {
                if (!markerRuntime.getReplacement().equals(replacement)) continue;

                if (folderItem == null) {
                    final FolderAdapter folderAdapter = new FolderAdapter(document.getName());
                    folderItem = new CheckBoxTreeItem<>(folderAdapter);
                    folderItem.setExpanded(true);
                    rootItem.getChildren().add(folderItem);
                }

                final Coords coords = document.getDocument().getLineIndex().getCoordsAt(markerRuntime.getPositionRange().getStart());
                final String line = document.getDocument().getLineIndex().getLine(coords.getLine(), document.getDocument().getText());
                final LeafAdapter leafAdapter = new LeafAdapter(markerRuntime, coords, line, document);
                final CheckBoxTreeItem<CheckboxAdapter> leafItem = new CheckBoxTreeItem<>(leafAdapter);
                folderItem.getChildren().add(leafItem);
            }
        }
    }

    private void handleCheckBoxChange(CheckBoxTreeItem.TreeModificationEvent<Object> event) {
        event.consume();
        if (!event.wasSelectionChanged()) {
            return;
        }

        final CheckBoxTreeItem<Object> treeItem = event.getTreeItem();
        final CheckboxAdapter adapter = (CheckboxAdapter) treeItem.getValue();
        adapter.toggle();

        doCountAndNotify();
    }

    private void doCountAndNotify() {
        final long count = treeView.getRoot().getChildren().stream()
                .map(TreeItem::getChildren)
                .flatMap(Collection::stream)
                .filter(checkboxAdapterTreeItem -> ((LeafAdapter) checkboxAdapterTreeItem.getValue()).selected)
                .count();
        listeners.forEach(listener -> listener.onCheckboxCount(count));
    }

    public void addSelectionListener(MarkerSelectListener listener) {
        listeners.add(listener);
    }

    private void notifySelectionListeners(TreeItem<CheckboxAdapter> item) {
        if (item == null) return;
        final CheckboxAdapter adapter = item.getValue();

        if (!(adapter instanceof LeafAdapter)) {
            listeners.forEach(MarkerSelectListener::onClearSelection);
            return;
        }

        final LeafAdapter leafAdapter = (LeafAdapter) adapter;
        listeners.forEach(markerSelectListener -> markerSelectListener.onSelectMarker(
                leafAdapter.marker, leafAdapter.coords, leafAdapter.document
        ));
    }

    public List<MarkerInDocument> getSelectedMarkers() {
        return treeView.getRoot().getChildren().stream()
                .map(TreeItem::getChildren)
                .flatMap(Collection::stream)
                .filter(checkboxAdapterTreeItem -> ((LeafAdapter) checkboxAdapterTreeItem.getValue()).selected)
                .map(checkboxAdapterTreeItem -> {
                    final LeafAdapter value = (LeafAdapter) checkboxAdapterTreeItem.getValue();
                    return new MarkerInDocument(value.marker, value.document);
                })
                .collect(Collectors.toList());
    }

    static class LeafAdapter implements CheckboxAdapter {

        private final MarkerRuntime marker;
        private final Coords coords;
        private final String line;
        private final AnonymizedFile document;
        private boolean selected;

        public LeafAdapter(MarkerRuntime marker, Coords coords, String line, AnonymizedFile document) {
            this.marker = marker;
            this.coords = coords;
            this.line = line;
            this.document = document;
        }

        @Override
        public void toggle() {
            selected = !selected;
        }

        @Override
        public String toString() {
            return coords.getLine() + ": " + line;
        }
    }

    public interface MarkerSelectListener {
        void onClearSelection();

        void onSelectMarker(MarkerRuntime marker, Coords coords, AnonymizedFile document);

        void onCheckboxCount(long count);
    }
}
