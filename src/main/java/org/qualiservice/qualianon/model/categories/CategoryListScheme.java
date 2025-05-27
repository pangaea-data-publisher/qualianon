package org.qualiservice.qualianon.model.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.gui.components.listimport.Importer;
import org.qualiservice.qualianon.model.properties.StringProperty;

import java.beans.Transient;
import java.io.File;
import java.util.Objects;


public class CategoryListScheme {

    private final StringProperty fileProperty;
    private SelectionStyle style;
    private Importer importer;

    public CategoryListScheme() {
        fileProperty = new StringProperty();
    }

    @JsonProperty
    public String getFile() {
        return fileProperty.getValue();
    }

    @JsonProperty
    public CategoryListScheme setFile(String file) {
        this.fileProperty.setValue(file);
        return this;
    }

    @Transient
    public StringProperty getFileProperty() {
        return fileProperty;
    }

    @JsonProperty
    public SelectionStyle getStyle() {
        return style;
    }

    @JsonProperty
    public CategoryListScheme setStyle(SelectionStyle style) {
        this.style = style;
        return this;
    }

    public Importer getImporter() {
        return importer;
    }

    public CategoryListScheme setImporter(Importer importer) {
        this.importer = importer;
        return this;
    }

    public CodingList loadList(File categoriesDirectories, MessageLogger messageLogger) {
        final File file = new File(categoriesDirectories.getAbsolutePath(), fileProperty.getValue());
        return importer.importFile(file, messageLogger);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryListScheme that = (CategoryListScheme) o;
        return Objects.equals(fileProperty, that.fileProperty) && style == that.style && importer == that.importer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileProperty, style, importer);
    }

    @Override
    public String toString() {
        return "CategoryListScheme{" +
                "file=" + fileProperty +
                ", style=" + style +
                ", importer=" + importer +
                '}';
    }
}
