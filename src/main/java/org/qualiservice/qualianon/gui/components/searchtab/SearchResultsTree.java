package org.qualiservice.qualianon.gui.components.searchtab;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.trees.CheckboxAdapter;
import org.qualiservice.qualianon.gui.tools.trees.FolderAdapter;
import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.project.SearchParams;
import org.qualiservice.qualianon.utility.StringUtils;

import java.util.List;


public class SearchResultsTree extends AnchorPane {

    private SearchResultSelectListener searchResultSelectListener;
    private final List<SearchResult> searchResults;

    public SearchResultsTree(SearchParams searchParams, List<SearchResult> results) {
        this.searchResults = results;

        final TreeView<CheckboxAdapter> treeView = new TreeView<>();
        treeView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, checkboxAdapterTreeItem, t1) -> notifySelectionListeners(t1)
        );
        getChildren().add(treeView);

        final String rootText = StringUtils.ellipsis(searchParams.getText(), 50) + ": " + results.size() + " occurrences (" + searchParams.getDescription() + ")";
        final CheckboxAdapter rootAdapter = new FolderAdapter(rootText);
        final CheckBoxTreeItem<CheckboxAdapter> rootItem = new CheckBoxTreeItem<>(rootAdapter);
        treeView.setRoot(rootItem);

        CheckBoxTreeItem<CheckboxAdapter> documentItem = null;
        String lastDocumentName = "";
        for (final SearchResult result : results) {
            if (documentItem == null || !result.getDocumentName().equals(lastDocumentName)) {
                documentItem = new CheckBoxTreeItem<>(
                        new FolderAdapter("Document " + result.getDocumentName())
                );
                documentItem.setExpanded(true);
                rootItem.getChildren().add(documentItem);
                lastDocumentName = result.getDocumentName();
            }
            final CheckBoxTreeItem<CheckboxAdapter> resultItem = new CheckBoxTreeItem<>(new LeafAdapter(result));
            documentItem.getChildren().add(resultItem);
            // This is necessary, so the treeview sets the states of parent nodes correctly
            resultItem.setSelected(!result.isSelected());
            resultItem.setSelected(result.isSelected());
        }

        rootItem.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), this::handleCheckBoxChange);
        rootItem.setExpanded(true);
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

        GuiUtility.extend(treeView);
    }

    public void addSelectionListener(SearchResultSelectListener listener) {
        searchResultSelectListener = listener;
        doCountAndNotify();
    }

    private void notifySelectionListeners(TreeItem<CheckboxAdapter> treeItem) {
        final CheckboxAdapter checkboxAdapter = treeItem.getValue();
        if (!(checkboxAdapter instanceof LeafAdapter)) {
            searchResultSelectListener.onClearSearchResult();
            return;
        }
        final SearchResult result = ((LeafAdapter) checkboxAdapter).getResult();
        searchResultSelectListener.onSelectSearchResult(result);
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
        final long count = searchResults.stream()
                .filter(SearchResult::isSelected)
                .count();
        if (searchResultSelectListener != null) {
            searchResultSelectListener.onCheckboxCount(count);
        }
    }


    static class LeafAdapter implements CheckboxAdapter {
        private final SearchResult result;

        LeafAdapter(SearchResult result) {
            this.result = result;
        }

        public void toggle() {
            result.setSelected(!result.isSelected());
        }

        public SearchResult getResult() {
            return result;
        }

        @Override
        public String toString() {
            return result.getLineContext();
        }
    }

    public interface SearchResultSelectListener {
        void onSelectSearchResult(SearchResult searchResult);

        void onClearSearchResult();

        void onCheckboxCount(long count);
    }

}
