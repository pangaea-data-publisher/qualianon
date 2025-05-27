package org.qualiservice.qualianon.listimport.claset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Classification {

    @JsonProperty("Label")
    private Label label;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty(value = "Item")
    private List<Item> itemList;

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

}
