package org.qualiservice.qualianon.listimport.claml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Title {

    @JacksonXmlText
    private String text;

    public String getText() {
        return text;
    }

}
