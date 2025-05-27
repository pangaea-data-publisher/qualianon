package org.qualiservice.qualianon.gui.components.listimport;

import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.listimport.claml.ClamlImporter;
import org.qualiservice.qualianon.listimport.claset.ClasetImporter;
import org.qualiservice.qualianon.listimport.sgtyp.SgtypImporter;
import org.qualiservice.qualianon.model.categories.CodingList;
import org.qualiservice.qualianon.model.categories.SelectionStyle;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;


public enum Importer {

    CUSTOM_XLSX((file, messageLogger) -> {
        try {
            return new CodingList(file);
        } catch (IOException e) {
            messageLogger.logError("Error importing custom xlsx file", e);
            return null;
        }
    }, SelectionStyle.LIST),

    CLASET((file, messageLogger) -> {
        try {
            return ClasetImporter.read(file);
        } catch (IOException e) {
            messageLogger.logError("Error importing Claset/XML file", e);
            return null;
        }
    }, SelectionStyle.TREE),

    CLAML((file, messageLogger) -> {
        try {
            return ClamlImporter.read(file);
        } catch (IOException e) {
            messageLogger.logError("Error importing ClaML/XML file", e);
            return null;
        }
    }, SelectionStyle.TREE),

    SGTYP((file, messageLogger) -> {
        try {
            return SgtypImporter.read(file);
        } catch (IOException e) {
            messageLogger.logError("Error importing SGTyp/XLSX file", e);
            return null;
        }
    }, SelectionStyle.LIST);

    private final BiFunction<File, MessageLogger, CodingList> importFn;
    private final SelectionStyle selectionStyle;

    Importer(BiFunction<File, MessageLogger, CodingList> importFn, SelectionStyle selectionStyle) {
        this.importFn = importFn;
        this.selectionStyle = selectionStyle;
    }

    public CodingList importFile(File file, MessageLogger messageLogger) {
        return importFn.apply(file, messageLogger);
    }

    public SelectionStyle getSelectionStyle() {
        return selectionStyle;
    }

    @Override
    public String toString() {
        return "Importer{" +
                "name=" + name() +
                ", selectionStyle=" + selectionStyle +
                '}';
    }
}
