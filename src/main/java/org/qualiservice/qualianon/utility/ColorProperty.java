package org.qualiservice.qualianon.utility;

import javafx.scene.paint.Color;

import java.util.Objects;


public class ColorProperty extends UpdatableImpl {

    private Color value;

    public ColorProperty() {
        value = Color.GRAY;
    }

    public ColorProperty(Color value) {
        this.value = value;
    }

    public Color getValue() {
        return value;
    }

    public void setValue(Color value) {
        this.value = value;
        notifyUpdateListeners(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorProperty that = (ColorProperty) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ColorProperty{" +
                "value=" + value +
                '}';
    }
}
