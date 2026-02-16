package org.qualiservice.qualianon.model.categories;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import javafx.scene.paint.Color;
import org.apache.poi.ss.util.WorkbookUtil;
import org.qualiservice.qualianon.model.properties.StringProperty;
import org.qualiservice.qualianon.utility.*;

import java.beans.Transient;
import java.util.List;
import java.util.Objects;


public class CategoryScheme extends UpdatableImpl {

    private final StringProperty name;
    private final ColorProperty color;
    private final ListProperty<LabelScheme> labels;
    private CategoryListScheme categoryList;

    /**
     * Returns a sheet-safe name for XLSX exports.
     */
    public static String makeSafeName(String name) {
        return WorkbookUtil.createSafeSheetName(name);
    }

    public CategoryScheme() {
        name = new StringProperty();
        name.addUpdateListener(isDirect -> onChildrenUpdated());
        labels = new ListProperty<>();
        labels.addUpdateListener(isDirect -> onChildrenUpdated());
        color = new ColorProperty();
        color.addUpdateListener(isDirect -> onChildrenUpdated());
    }

    private void onChildrenUpdated() {
        notifyUpdateListeners(false);
    }

    public CategoryScheme(String name) {
        this();
        this.name.setValue(name);
    }

    public String getName() {
        return name.getValue();
    }

    public CategoryScheme setName(String name) {
        this.name.setValue(name);
        return this;
    }

    @Transient
    public StringProperty getNameProperty() {
        return name;
    }

    @JacksonXmlProperty(isAttribute = true)
    @JsonSerialize(using = ColorSerializer.class)
    public Color getColor() {
        return color.getValue();
    }

    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = ColorDeserializer.class)
    public CategoryScheme setColor(Color color) {
        this.color.setValue(color);
        return this;
    }

    @Transient
    public ColorProperty getColorProperty() {
        return color;
    }

    public boolean hasColor() {
        return color != null;
    }

    public boolean hasLabel(String parameter) {
        if (labels == null) return false;
        for (final LabelScheme label : labels) {
            if (label.getName().equals(parameter)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLabels() {
        return labels != null && labels.size() > 0;
    }

    /**
     * Returns the list of label schemes in display/order sequence.
     */
    @JacksonXmlElementWrapper(localName = "labels")
    @JacksonXmlProperty(localName = "label")
    public List<LabelScheme> getLabels() {
        return labels;
    }

    /**
     * Replaces label schemes in bulk, preserving update semantics.
     */
    @JacksonXmlElementWrapper(localName = "labels")
    @JacksonXmlProperty(localName = "label")
    public void setLabels(List<LabelScheme> labels) {
        this.labels.beginBatch();
        this.labels.clear();
        this.labels.addAll(labels);
        this.labels.endBatch();
    }

    @Transient
    public ListProperty<LabelScheme> getLabelsProperty() {
        return labels;
    }

    public CategoryScheme addLabel(LabelScheme label) {
        labels.add(label);
        return this;
    }

    public CategoryScheme removeLabel(LabelScheme label) {
        labels.remove(label);
        return this;
    }

    public LabelScheme findLabel(String name) {
        return labels.stream()
                .filter(labelScheme -> labelScheme.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    /**
     * Returns the category list definition, if configured.
     */
    public CategoryListScheme getCategoryList() {
        return categoryList;
    }

    /**
     * Links a category to an external coding list definition.
     */
    public CategoryScheme setCategoryList(CategoryListScheme categoryList) {
        this.categoryList = categoryList;
        notifyUpdateListeners(true);
        return this;
    }

    /**
     * Indicates whether this category uses an external coding list.
     */
    public boolean hasCategoryList() {
        return categoryList != null && !StringUtils.isBlank(categoryList.getFile());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryScheme that = (CategoryScheme) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "CategoryScheme{" +
                "name=" + name +
                ", color=" + color +
                ", labels=" + labels +
                ", categoryList=" + categoryList +
                '}';
    }

}
