package org.qualiservice.qualianon.model.project;

import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.model.properties.StringProperty;
import org.qualiservice.qualianon.utility.StringUtils;
import org.qualiservice.qualianon.utility.UpdatableImpl;
import org.qualiservice.qualianon.utility.UpdateListener;

import java.util.*;


public class Replacement extends UpdatableImpl {

    private UUID id;
    private CategoryScheme categoryScheme;
    private List<Label> labels = new LinkedList<>();
    private final MessageLogger messageLogger;
    private final StringProperty labelsTextBlock;
    private final UpdateListener updateListener;
    private final HashMap<UUID, UpdateListener> labelUpdateListeners;


    public Replacement(CategoryScheme categoryScheme, MessageLogger messageLogger) {
        updateListener = isDirect -> notifyUpdateListeners(false);
        this.messageLogger = messageLogger;
        this.id = UUID.randomUUID();
        this.categoryScheme = categoryScheme;
        this.categoryScheme.getNameProperty().addUpdateListener(updateListener);
        labelsTextBlock = new StringProperty();
        labelUpdateListeners = new HashMap<>();
    }

    public Replacement(Replacement replacement, MessageLogger messageLogger) {
        this(replacement.getCategoryScheme(), messageLogger);
        id = replacement.getId();
        labels = replacement.getLabels();
        labelsTextBlock.setValue(getLabelsTextBlock());
        bindLabels(labels);
    }

    public UUID getId() {
        return id;
    }

    public Replacement setId(UUID id) {
        this.id = id;
        return this;
    }

    public CategoryScheme getCategoryScheme() {
        return categoryScheme;
    }

    public Replacement addLabel(Label label) {
        labels.add(label);
        bindLabels(Collections.singletonList(label));
        notifyUpdateListeners(true);
        return this;
    }

    /**
     * Sets the labels once for a new replacement; cannot overwrite existing labels.
     */
    // Only called on new replacement, so labels is empty before
    public Replacement setLabels(List<Label> labels) {
        if (!this.labels.isEmpty()) throw new IllegalStateException("labels must be empty");
        this.labels = labels;
        bindLabels(labels);
        notifyUpdateListeners(true);
        return this;
    }

    public void removeLabel(Label label) {
        unbindLabels(Collections.singletonList(label));
        labels.remove(label);
        notifyUpdateListeners(true);
    }

    /**
     * Updates this replacement from another instance and rebinds listeners.
     */
    public void update(Replacement other) {
        unbindLabels(labels);
        categoryScheme = other.getCategoryScheme();
        labels = other.getLabels();
        bindLabels(labels);
        notifyUpdateListeners(true);
    }

    /**
     * Binds label listeners to track changes and keep label text cache updated.
     */
    private void bindLabels(List<Label> labels) {
        labels.forEach(label -> {
            label.addUpdateListener(updateListener);
            final LabelScheme labelScheme = categoryScheme.findLabel(label.getLevel());
            if (labelScheme == null) {
                messageLogger.logError("Cannot find label in categories: " + label.getLevel(), null);
                return;
            }
            final UpdateListener updateListener = isDirect -> {
                label.setLevel(labelScheme.getName());
                labelsTextBlock.setValue(getLabelsTextBlock());
            };
            labelUpdateListeners.put(label.getId(), updateListener);
            labelScheme.getNameProperty().addUpdateListener(updateListener);
        });
    }

    /**
     * Removes label listeners when labels are detached or replaced.
     */
    private void unbindLabels(List<Label> labels) {
        labels.forEach(label -> {
            label.removeUpdateListener(updateListener);
            final LabelScheme labelScheme = categoryScheme.findLabel(label.getLevel());
            if (labelScheme == null) {
                messageLogger.logError("Cannot find label in categories: " + label.getLevel(), null);
                return;
            }
            final UpdateListener updateListener = labelUpdateListeners.remove(label.getId());
            labelScheme.getNameProperty().removeUpdateListener(updateListener);
        });
    }

    public List<Label> getLabels() {
        return labels;
    }

    /**
     * Returns labels formatted as a multi-line block for display.
     */
    public String getLabelsTextBlock() {
        return getLabelsString("\n");
    }

    public StringProperty getLabelsTextBlockProperty() {
        return labelsTextBlock;
    }

    /**
     * Returns labels formatted as a single-line summary.
     */
    public String getLabelsTextLine() {
        return getLabelsString(", ");
    }

    private String getLabelsString(String separator) {
        return labels.stream()
                .filter(label -> label.getValue() != null)
                .filter(label -> !label.getValue().isEmpty())
                .map(label -> label.getLevel() + ": " + label.getValue())
                .reduce((s1, s2) -> s1 + separator + s2)
                .orElse("<none>");
    }

    public boolean isLabelUsed(String labelName) {
        return getLabels().stream()
                .filter(label1 -> label1.getLevel().equals(labelName))
                .anyMatch(label1 -> !StringUtils.isBlank(label1.getValue()));
    }

    public Label getLabel(String labelName) {
        return getLabels().stream()
                .filter(label1 -> label1.getLevel().equals(labelName))
                .findAny()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Replacement that = (Replacement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Replacement{" +
                "id=" + id +
                ", categoryScheme=" + categoryScheme +
                ", labels=" + labels +
                '}';
    }
}
