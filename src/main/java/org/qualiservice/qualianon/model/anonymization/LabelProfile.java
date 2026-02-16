package org.qualiservice.qualianon.model.anonymization;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.qualiservice.qualianon.model.properties.BooleanProperty;

import java.beans.Transient;
import java.util.Objects;


public class LabelProfile {

    private String name;
    private final BooleanProperty enabled;

    public LabelProfile() {
        enabled = new BooleanProperty();
    }

    public LabelProfile(String name, boolean enabled) {
        this();
        this.name = name;
        this.enabled.setValue(enabled);
    }

    public String getName() {
        return name;
    }

    public LabelProfile setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Indicates whether this label is included in export output.
     */
    @JacksonXmlProperty
    public boolean isEnabled() {
        return enabled.isValue();
    }

    @JacksonXmlProperty
    public LabelProfile setEnabled(boolean enabled) {
        this.enabled.setValue(enabled);
        return this;
    }

    @Transient
    public BooleanProperty getProperty() {
        return enabled;
    }

    /**
     * Marks the enabled flag as persisted after saving.
     */
    public void setPersisted() {
        enabled.setPersisted();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelProfile that = (LabelProfile) o;
        return Objects.equals(name, that.name) && Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, enabled);
    }

    @Override
    public String toString() {
        return "LabelProfile{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
