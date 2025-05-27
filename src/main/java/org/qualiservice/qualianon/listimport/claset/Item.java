package org.qualiservice.qualianon.listimport.claset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    private String id;
    private int idLevel;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty(value = "Label")
    private List<Label> labelList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdLevel() {
        return idLevel;
    }

    public void setIdLevel(int idLevel) {
        this.idLevel = idLevel;
    }

    public List<Label> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<Label> labelList) {
        this.labelList = labelList;
    }

    public String getValue(String language) {
        return labelList.stream()
                .filter(label -> label.getLabelText().getLanguage().equals(language))
                .map(label -> label.getLabelText().getText())
                .findAny()
                .orElse("");
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", idLevel='" + idLevel + '\'' +
                ", labelList=" + labelList +
                '}';
    }
}
