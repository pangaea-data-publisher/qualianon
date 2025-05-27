package org.qualiservice.qualianon.gui.tools.tabs;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.qualiservice.qualianon.gui.tools.GuiUtility;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class CollapsingTabPane extends AnchorPane {

    private final TabPane tabPane;
    private boolean isCollapsed;
    private final List<OpenPaneListener> listeners;


    public CollapsingTabPane(boolean collapseWhenEmpty) {
        setMaxHeight(0f);
        isCollapsed = true;
        listeners = new LinkedList<>();

        tabPane = new TabPane();
        // TODO: replacement???
        //tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        setMinHeight(100f);

        if (collapseWhenEmpty) {
            tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
                if (t1 == null) {
                    setMaxHeight(0f);
                    setMinHeight(0f);
                    isCollapsed = true;
                } else if (tabPane.getTabs().size() == 1 && isCollapsed) {
                    setMaxHeight(Double.MAX_VALUE);
                    setMinHeight(100f);
                    isCollapsed = false;
                    notifyListeners();
                }
            });
        } else {
            setMaxHeight(Double.MAX_VALUE);
            setMinHeight(100f);
        }

        getChildren().add(tabPane);
        GuiUtility.extend(this);
        GuiUtility.extend(tabPane);
    }

    public void addOpenPaneListener(OpenPaneListener listener) {
        listeners.add(listener);
    }

    public void openTab(BaseTab baseTab) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getContent() == baseTab) {
                // Show existing tab
                tabPane.getSelectionModel().select(i);
                return;
            }
        }

        // Add new tab
        final Tab tab = new Tab(baseTab.getTitle(), baseTab);
        tab.setOnClosed(t -> baseTab.onClose());
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
    }

    public List<BaseTab> getTabs() {
        return tabPane.getTabs().stream()
                .map(tab -> (BaseTab) tab.getContent())
                .collect(Collectors.toList());
    }

    public void closeAllTabs() {
        while (tabPane.getTabs().size() > 0) {
            closeTab(tabPane.getTabs().get(0));
        }
    }

    public void closeActiveTab() {
        if (isCollapsed) return;
        closeTab(tabPane.getSelectionModel().getSelectedItem());
    }

    public void closeTab(BaseTab baseTab) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            final Tab tab = tabPane.getTabs().get(i);
            if (tab.getContent() == baseTab) {
                closeTab(tab);
                return;
            }
        }
    }

    private void closeTab(Tab tab) {
        final EventHandler<Event> onClosed = tab.getOnClosed();
        if (onClosed != null) {
            onClosed.handle(null);
        }
        tabPane.getTabs().remove(tab);
    }

    public BaseTab getSelectedTab() {
        final Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return null;
        return (BaseTab) selectedItem.getContent();
    }

    private void notifyListeners() {
        listeners.forEach(OpenPaneListener::onOpen);
    }

    public interface OpenPaneListener {
        void onOpen();
    }
}
