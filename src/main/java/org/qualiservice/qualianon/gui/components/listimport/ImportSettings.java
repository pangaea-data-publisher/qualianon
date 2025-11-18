package org.qualiservice.qualianon.gui.components.listimport;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import org.qualiservice.qualianon.utility.YJFileChooser;

import java.util.List;


public class ImportSettings {

    private final String title;
    private final List<Node> instructions;
    private final FileChooser.ExtensionFilter extensionFilter;
    private final Importer importer;

    public ImportSettings(String title, List<Node> instructions, FileChooser.ExtensionFilter extensionFilter, Importer importer) {
        this.title = title;
        this.instructions = instructions;
        this.extensionFilter = extensionFilter;
        this.importer = importer;
    }

    public String getTitle() {
        return title;
    }

    public List<Node> getInstructions() {
        return instructions;
    }

    public FileChooser.ExtensionFilter getExtensionFilter() {
        return extensionFilter;
    }

    public Importer getImporter() {
        return importer;
    }

    @Override
    public String toString() {
        return title;
    }
}
