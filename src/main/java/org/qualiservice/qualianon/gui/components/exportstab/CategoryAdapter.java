package org.qualiservice.qualianon.gui.components.exportstab;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.anonymization.CategoryProfile;
import org.qualiservice.qualianon.utility.StringUtils;


public class CategoryAdapter implements ItemAdapter {

    private final Label label;

    public CategoryAdapter(CategoryProfile categoryProfile) {
        label = new Label();
        update(categoryProfile);
        categoryProfile.addUpdateListener((boolean isDirect) -> update(categoryProfile));
    }

    private void update(CategoryProfile categoryProfile) {
        label.setText(StringUtils.textWithIndicator(
                "Category: " + categoryProfile.getCategoryName(),
                categoryProfile.isModified()
        ));
    }

    public Node getNode() {
        return label;
    }

}
