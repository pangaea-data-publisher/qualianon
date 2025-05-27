package org.qualiservice.qualianon.gui.components.listlookup;

import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.categories.CodingList;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Map;

public interface ListLookupWindow {

    static Map<String, String> openWindow(CategoryScheme categoryScheme, CodingList codingList, String selectedText, Window owner, MessageLogger logger) {
        try {
            final FXMLLoader fxmlLoader = getFxmlLoaderForList(categoryScheme);
            final Parent root = fxmlLoader.load();
            final ListLookupWindow controller = fxmlLoader.getController();
            controller.createWindow(selectedText, codingList, categoryScheme);
            final Scene scene = new Scene(root);
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(categoryScheme.getName() + " List Lookup");
            stage.setResizable(true);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.initOwner(owner);
            stage.showAndWait();
            return controller.getSelectedInfo();
        } catch (IOException e) {
            logger.logError("Open window error", e);
            return null;
        }
    }

    static FXMLLoader getFxmlLoaderForList(CategoryScheme categoryScheme) {
        switch (categoryScheme.getCategoryList().getStyle()) {
            case TREE:
                return new FXMLLoader(ListLookupWindow.class.getClassLoader().getResource("listLookupAsTree.fxml"));
            case LIST:
            default:
                return new FXMLLoader(ListLookupWindow.class.getClassLoader().getResource("singleListMarker.fxml"));
        }
    }


    void createWindow(String pattern, CodingList codingList, CategoryScheme category);

    Map<String, String> getSelectedInfo();

}
