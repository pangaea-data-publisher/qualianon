package org.qualiservice.qualianon.listimport.claset;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;


public class LabelText {

    private String language;

    @JacksonXmlText
    private String text;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "LabelText{" +
                "language='" + language + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
