package org.qualiservice.qualianon.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import javafx.scene.paint.Color;

import java.io.IOException;


public class ColorDeserializer extends JsonDeserializer<Color> {

    @Override
    public Color deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Color.valueOf(jsonParser.readValueAs(String.class));
    }

}
