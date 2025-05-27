package org.qualiservice.qualianon.model.project;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import org.qualiservice.qualianon.utility.UpdatableImpl;

import java.beans.Transient;
import java.util.Objects;
import java.util.UUID;


public class Label extends UpdatableImpl {

    @JacksonXmlProperty(isAttribute = true)
    private String level;
    @JacksonXmlText
    private String value;

    private final UUID id = UUID.randomUUID();

    public Label() {
    }

    public Label(String level, String value) {
        this.level = level;
        this.value = value;
    }

    public String getLevel() {
        return level;
    }

    public Label setLevel(String level) {
        this.level = level;
        notifyUpdateListeners(true);
        return this;
    }

    public String getValue() {
        return value;
    }

    public Label setValue(String value) {
        this.value = value;
        notifyUpdateListeners(true);
        return this;
    }

    @Transient
    public UUID getId() {
        return id;
    }

    @Transient
    public String getExportText() {
        return level + ": " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return Objects.equals(level, label.level) &&
                Objects.equals(value, label.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, value);
    }

    @Override
    public String toString() {
        return "Label{" +
                "level='" + level + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
