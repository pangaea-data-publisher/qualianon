package org.qualiservice.qualianon.model.properties;

import org.qualiservice.qualianon.utility.UpdatableImpl;

import java.util.Objects;


public class StringProperty extends UpdatableImpl {

    private String value;

    public StringProperty() {
    }

    public StringProperty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        notifyUpdateListeners(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringProperty that = (StringProperty) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "StringProperty{" +
                "value='" + value + '\'' +
                '}';
    }
}
