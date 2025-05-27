package org.qualiservice.qualianon.listimport.claml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ClamlClass {

    @JsonProperty("code")
    private String code;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("SuperClass")
    private SuperClass superClass;

    @JsonProperty("SubClass")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SubClass> subClasses;

    @JsonProperty("Rubric")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Rubric> rubrics;

    public List<Rubric> getRubrics() {
        return rubrics;
    }

    public String getCode() {
        return code;
    }

    public SuperClass getSuperClass() {
        return superClass;
    }

    public List<SubClass> getSubClasses() {
        return subClasses;
    }

}
