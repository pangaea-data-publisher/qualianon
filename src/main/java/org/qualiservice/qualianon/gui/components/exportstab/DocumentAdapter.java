package org.qualiservice.qualianon.gui.components.exportstab;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.project.AnonymizedFile;

import java.util.List;


class DocumentAdapter implements ItemAdapter {

    private final HBox hBox;
    private final Label label;

    DocumentAdapter(AnonymizedFile document, Export export, List<ExportsTreeComponent.AnonymizedDocumentSelectionListener> listeners) {
        label = new Label();
        hBox = new HBox(label);
        GuiUtility.setOnDoubleClick(hBox, () ->
                listeners.forEach(listener -> listener.onAnonymizedDocumentSelected(document, export))
        );
        update(document);
        document.addUpdateListener((boolean isDirect) -> update(document));
    }

    private void update(AnonymizedFile document) {
        label.setText(document.getName());
    }

    @Override
    public Node getNode() {
        return hBox;
    }

}
