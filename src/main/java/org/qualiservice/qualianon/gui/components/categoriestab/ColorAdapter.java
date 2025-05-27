package org.qualiservice.qualianon.gui.components.categoriestab;

import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.commands.SetColorPropertyCommand;
import org.qualiservice.qualianon.utility.ColorProperty;


public class ColorAdapter implements ItemAdapter {

    private final HBox hBox;

    public ColorAdapter(ColorProperty colorProperty, CommandRunner commandRunner) {
        final ColorPicker colorPicker = new ColorPicker(colorProperty.getValue());
        colorPicker.setOnAction(actionEvent -> {
            final SetColorPropertyCommand command = new SetColorPropertyCommand(colorProperty, colorPicker.getValue(), "for category.");
            commandRunner.runCommand(command);
        });

        colorProperty.addUpdateListener((boolean isDirect) -> colorPicker.setValue(colorProperty.getValue()));

        hBox = new HBox(new Label("Color:"), colorPicker);
        hBox.setSpacing(7f);
    }

    public Node getNode() {
        return hBox;
    }

}
