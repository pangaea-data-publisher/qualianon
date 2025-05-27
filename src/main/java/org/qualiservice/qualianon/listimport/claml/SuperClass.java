package org.qualiservice.qualianon.listimport.claml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SuperClass {

    @JsonProperty("code")
    private String code;

    public String getCode() {
        return code;
    }
}
