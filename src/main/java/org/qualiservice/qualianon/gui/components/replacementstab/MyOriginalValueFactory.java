package org.qualiservice.qualianon.gui.components.replacementstab;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;

public class MyOriginalValueFactory implements javafx.util.Callback<javafx.scene.control.TreeTableColumn.CellDataFeatures<ReplacementListComponent.MyItem, String>, javafx.beans.value.ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ReplacementListComponent.MyItem, String> myItemStringCellDataFeatures) {
        return myItemStringCellDataFeatures.getValue().getValue().getOriginal();
    }
}
