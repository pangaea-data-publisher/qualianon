package org.qualiservice.qualianon.listimport.claset;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Label {

    private String qualifier;

    @JsonProperty("LabelText")
    private LabelText labelText;

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public LabelText getLabelText() {
        return labelText;
    }

    public void setLabelText(LabelText labelText) {
        this.labelText = labelText;
    }

    @Override
    public String toString() {
        return "Label{" +
                "qualifier='" + qualifier + '\'' +
                ", labelText=" + labelText +
                '}';
    }
}
