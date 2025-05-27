package org.qualiservice.qualianon.listimport.claml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Rubric {

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("Label")
    private Label label;

    public String getKind() {
        return kind;
    }

    public Label getLabel() {
        return label;
    }

}
