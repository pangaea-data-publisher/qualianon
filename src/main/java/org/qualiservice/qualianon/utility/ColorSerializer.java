package org.qualiservice.qualianon.utility;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.scene.paint.Color;

import java.io.IOException;


public class ColorSerializer extends JsonSerializer<Color> {

    @Override
    public void serialize(Color color, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(color.toString());
    }

}
