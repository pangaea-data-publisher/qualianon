package org.qualiservice.qualianon.listimport.claset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Claset {

    @JsonProperty(value = "Classification")
    private Classification classification;

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    @Override
    public String toString() {
        return "IscoXml{" +
                "classification=" + classification +
                '}';
    }
}
