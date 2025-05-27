package org.qualiservice.qualianon.model.project;

import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.properties.BooleanProperty;
import org.qualiservice.qualianon.utility.ListProperty;
import org.qualiservice.qualianon.utility.UpdatableImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


public class ReplacementCollection extends UpdatableImpl {

    private final ListProperty<Replacement> replacements;
    private final BooleanProperty modifiedProperty;


    public ReplacementCollection() {
        super();
        modifiedProperty = new BooleanProperty(false);
        replacements = new ListProperty<>();
        replacements.addUpdateListener(isDirect -> {
            modifiedProperty.setValue(true);
            notifyUpdateListeners(false);
        });
    }

    public List<CategoryScheme> getCategories() {
        return replacements.stream()
                .map(Replacement::getCategoryScheme)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()).stream()
                .sorted(Comparator.comparing(CategoryScheme::getName))
                .collect(Collectors.toList());
    }

    public List<Replacement> getForCategory(CategoryScheme categoryScheme) {
        return replacements.stream()
                .filter(replacement -> replacement.getCategoryScheme() != null)
                .filter(replacement -> replacement.getCategoryScheme().equals(categoryScheme))
                .collect(Collectors.toList());
    }

    public Replacement getById(UUID id) {
        return replacements.stream()
                .filter(replacement -> replacement.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public ReplacementCollection add(Replacement replacement) {
        replacements.add(replacement);
        return this;
    }

    public void remove(Replacement replacement) {
        replacements.remove(replacement);
    }

    public void updateReplacement(Replacement replacement) {
        getById(replacement.getId())
                .update(replacement);
    }

    public List<Replacement> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<Replacement> replacements) {
        this.replacements.beginBatch();
        this.replacements.clear();
        this.replacements.addAll(replacements);
        this.replacements.endBatch();
    }

    public BooleanProperty getModifiedProperty() {
        return modifiedProperty;
    }

    public boolean isModified() {
        return modifiedProperty.isValue();
    }

    public void setModified(boolean modified) {
        modifiedProperty.setValue(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplacementCollection that = (ReplacementCollection) o;
        return Objects.equals(replacements, that.replacements) && Objects.equals(listeners, that.listeners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replacements, listeners);
    }

    @Override
    public String toString() {
        return "ReplacementCollection{" +
                "replacements=" + replacements +
                ", listeners=" + listeners +
                '}';
    }
}
