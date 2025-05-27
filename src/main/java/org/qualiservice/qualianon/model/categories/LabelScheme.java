package org.qualiservice.qualianon.model.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.qualiservice.qualianon.model.properties.StringProperty;
import org.qualiservice.qualianon.utility.ListProperty;
import org.qualiservice.qualianon.utility.UpdatableImpl;

import java.beans.Transient;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class LabelScheme extends UpdatableImpl {

    private final StringProperty name;
    private final ListProperty<StringProperty> choices;

    public LabelScheme() {
        name = new StringProperty();
        name.addUpdateListener(isDirect -> notifyUpdateListeners(false));
        choices = new ListProperty<>();
        choices.addUpdateListener(isDirect -> notifyUpdateListeners(false));
    }

    public LabelScheme(String name) {
        this();
        this.name.setValue(name);
    }

    public String getName() {
        return name.getValue();
    }

    public LabelScheme setName(String name) {
        this.name.setValue(name);
        return this;
    }

    @Transient
    public StringProperty getNameProperty() {
        return name;
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "choices")
    @JacksonXmlProperty(localName = "choice")
    public List<String> getChoices() {
        return choices.stream()
                .map(StringProperty::getValue)
                .collect(Collectors.toList());
    }

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "choices")
    @JacksonXmlProperty(localName = "choice")
    public void setChoices(List<String> choices) {
        final List<StringProperty> collect = choices.stream()
                .map(StringProperty::new)
                .collect(Collectors.toList());
        this.choices.beginBatch();
        this.choices.clear();
        this.choices.addAll(collect);
        this.choices.endBatch();
    }

    @Transient
    public ListProperty<StringProperty> getChoicesProperty() {
        return choices;
    }

    public LabelScheme addChoice(StringProperty choice, int index) {
        if (index == -1) {
            choices.add(choice);
        } else {
            choices.add(index, choice);
        }
        return this;
    }

    public LabelScheme addChoice(StringProperty stringProperty) {
        return addChoice(stringProperty, -1);
    }

    public LabelScheme addChoice(String choice) {
        return addChoice(new StringProperty(choice));
    }

    public void removeChoice(StringProperty choice) {
        choices.remove(choice);
    }

    public boolean hasChoices() {
        return choices.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelScheme that = (LabelScheme) o;
        return Objects.equals(name, that.name) && Objects.equals(choices, that.choices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, choices);
    }

    @Override
    public String toString() {
        return "LabelScheme{" +
                "name=" + name +
                ", choices=" + choices +
                '}';
    }
}
