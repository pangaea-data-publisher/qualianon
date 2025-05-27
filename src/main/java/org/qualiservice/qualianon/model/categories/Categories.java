package org.qualiservice.qualianon.model.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.properties.BooleanProperty;
import org.qualiservice.qualianon.utility.ListProperty;
import org.qualiservice.qualianon.utility.UpdatableImpl;
import org.qualiservice.qualianon.utility.UpdateListener;

import java.beans.Transient;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.qualiservice.qualianon.utility.StringUtils.getUniqueName;


@JsonRootName(value = "categories")
public class Categories extends UpdatableImpl {

    private final ListProperty<CategoryScheme> categories;

    private final HashMap<String, CodingList> codingLists;
    private File categoriesDirectory;
    private MessageLogger messageLogger;
    private final BooleanProperty modifiedProperty;

    public Categories() {
        codingLists = new HashMap<>();
        categories = new ListProperty<>();
        modifiedProperty = new BooleanProperty(false);
        addUpdateListener(isDirect -> {
            modifiedProperty.setValue(true);
            onUpdateChildren();
        });
    }

    public void addUpdateListener(UpdateListener updateListener) {
        categories.addUpdateListener(updateListener);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty(value = "category")
    public List<CategoryScheme> getCategories() {
        return categories;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty(value = "category")
    public Categories setCategories(List<CategoryScheme> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        return this;
    }

    @Transient
    public ListProperty<CategoryScheme> getCategoriesProperty() {
        return categories;
    }

    public CategoryScheme getByName(String name) {
        for (final CategoryScheme categoryScheme : categories) {
            if (categoryScheme.getName().equals(name)) return categoryScheme;
        }
        return null;
    }

    public void init(File categoriesDirectory, MessageLogger messageLogger) {
        this.categoriesDirectory = categoriesDirectory;
        this.messageLogger = messageLogger;
        importCodingLists();
    }

    private void importCodingLists() {
        for (final CategoryScheme categoryScheme : categories) {
            if (!categoryScheme.hasCategoryList()) continue;

            final String filename = categoryScheme.getCategoryList().getFile();
            if (codingLists.containsKey(filename)) continue;

            final CodingList codingList = categoryScheme.getCategoryList().loadList(categoriesDirectory, messageLogger);
            codingLists.put(filename, codingList);
        }
    }

    public CodingList getCodingList(CategoryScheme categoryScheme) {
        return codingLists.get(categoryScheme.getCategoryList().getFile());
    }

    private void onUpdateChildren() {
        if (categoriesDirectory == null) return; // Init not called

        importCodingLists();
        notifyUpdateListeners(false);
    }

    public String makeNewName() {
        final List<String> names = getCategoriesProperty().stream()
                .map(CategoryScheme::getName)
                .collect(Collectors.toList());
        return getUniqueName(names, "New category");
    }

    @Transient
    public BooleanProperty getModifiedProperty() {
        return modifiedProperty;
    }

    @Transient
    public boolean isModified() {
        return modifiedProperty.isValue();
    }

    @Transient
    public void setModified(boolean modified) {
        modifiedProperty.setValue(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categories that = (Categories) o;
        return Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categories);
    }
}
