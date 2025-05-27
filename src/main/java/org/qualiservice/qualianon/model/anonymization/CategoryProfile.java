package org.qualiservice.qualianon.model.anonymization;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.qualiservice.qualianon.model.properties.BooleanProperty;
import org.qualiservice.qualianon.utility.UpdatableImpl;
import org.qualiservice.qualianon.utility.UpdateListener;

import java.beans.Transient;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class CategoryProfile extends UpdatableImpl {

    private final UpdateListener updateListener;
    private String categoryName;

    @JacksonXmlElementWrapper(localName = "labels")
    @JacksonXmlProperty(localName = "label")
    private List<LabelProfile> labelProfiles;

    private final BooleanProperty countingEnabled;
    private final BooleanProperty originalEnabled;

    private boolean modified;


    public CategoryProfile() {
        labelProfiles = new LinkedList<>();
        updateListener = (boolean isDirect) -> {
            modified = calcModifiedStatus();
            notifyUpdateListeners(false);
        };
        countingEnabled = new BooleanProperty();
        countingEnabled.addUpdateListener(updateListener);
        originalEnabled = new BooleanProperty();
        originalEnabled.addUpdateListener(updateListener);
    }

    public CategoryProfile(String categoryName) {
        this();
        this.categoryName = categoryName;
    }

    @JacksonXmlProperty
    public String getCategoryName() {
        return categoryName;
    }

    @JacksonXmlProperty
    public CategoryProfile setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        notifyUpdateListeners(true);
        return this;
    }

    public List<LabelProfile> getLabelProfiles() {
        return labelProfiles;
    }

    public CategoryProfile setLabelProfiles(List<LabelProfile> labelProfiles) {
        if (labelProfiles != null) {
            this.labelProfiles = labelProfiles;
        }
        this.labelProfiles.forEach(labelProfile -> labelProfile.getProperty().addUpdateListener(updateListener));
        notifyUpdateListeners(true);
        return this;
    }

    public CategoryProfile addLabelProfile(LabelProfile labelProfile) {
        labelProfiles.add(labelProfile);
        labelProfile.getProperty().addUpdateListener(updateListener);
        notifyUpdateListeners(true);
        return this;
    }

    public boolean isLabelEnabled(String label) {
        if (labelProfiles == null) return false;
        return labelProfiles.stream()
                .filter(labelProfile -> labelProfile.getName().equals(label))
                .findAny()
                .orElse(new LabelProfile("", false))
                .isEnabled();
    }

    @JacksonXmlProperty
    public boolean isCountingEnabled() {
        return countingEnabled.isValue();
    }

    @JacksonXmlProperty
    public CategoryProfile setCountingEnabled(boolean enabled) {
        countingEnabled.setValue(enabled);
        modified = calcModifiedStatus();
        notifyUpdateListeners(true);
        return this;
    }

    @Transient
    public BooleanProperty getCountingProperty() {
        return countingEnabled;
    }

    @JacksonXmlProperty
    public boolean isOriginalEnabled() {
        return originalEnabled.isValue();
    }

    @JacksonXmlProperty
    public CategoryProfile setOriginalEnabled(boolean enabled) {
        originalEnabled.setValue(enabled);
        modified = calcModifiedStatus();
        return this;
    }

    @Transient
    public BooleanProperty getOriginalProperty() {
        return originalEnabled;
    }

    @Transient
    public boolean isModified() {
        return modified;
    }

    public void setPersisted() {
        originalEnabled.setPersisted();
        countingEnabled.setPersisted();
        labelProfiles.forEach(LabelProfile::setPersisted);
        modified = false;
    }

    private boolean calcModifiedStatus() {
        return originalEnabled.isModified() ||
                countingEnabled.isModified() ||
                labelProfiles.stream()
                        .map(labelProfile -> labelProfile.getProperty().isModified())
                        .reduce((b1, b2) -> b1 || b2)
                        .orElse(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryProfile that = (CategoryProfile) o;
        return Objects.equals(categoryName, that.categoryName) && Objects.equals(labelProfiles, that.labelProfiles) && Objects.equals(countingEnabled, that.countingEnabled) && Objects.equals(originalEnabled, that.originalEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryName, labelProfiles, countingEnabled, originalEnabled);
    }

    @Override
    public String toString() {
        return "CategoryProfile{" +
                "categoryName='" + categoryName + '\'' +
                ", labelProfiles=" + labelProfiles +
                ", countingEnabled=" + countingEnabled +
                ", originalEnabled=" + originalEnabled +
                '}';
    }
}
