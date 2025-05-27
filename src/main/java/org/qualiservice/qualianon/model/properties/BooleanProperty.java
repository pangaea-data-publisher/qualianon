package org.qualiservice.qualianon.model.properties;

import org.qualiservice.qualianon.utility.UpdatableImpl;

import java.util.Objects;


public class BooleanProperty extends UpdatableImpl {

    private boolean persistedValue;
    private boolean value;

    public BooleanProperty() {
    }

    public BooleanProperty(boolean value) {
        this.value = value;
        persistedValue = value;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
        notifyUpdateListeners(true);
    }

    public void setPersisted() {
        persistedValue = value;
        notifyUpdateListeners(true);
    }

    public boolean isModified() {
        return value != persistedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanProperty that = (BooleanProperty) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BooleanProperty{" +
                "value=" + value +
                '}';
    }
}
