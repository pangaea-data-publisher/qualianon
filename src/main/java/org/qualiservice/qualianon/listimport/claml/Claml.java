package org.qualiservice.qualianon.listimport.claml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Claml {

    @JsonProperty("Title")
    private Title title;

    // Field is not necessary, but deserialization fails without (DE version)
    @JsonProperty("ModifierClass")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ModifierClass> modifierClasses;

    @JsonProperty("Class")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ClamlClass> classes;

    public Title getTitle() {
        return title;
    }

    public List<ClamlClass> getClasses() {
        return classes;
    }

}
