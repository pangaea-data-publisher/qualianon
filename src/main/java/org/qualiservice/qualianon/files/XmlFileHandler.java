package org.qualiservice.qualianon.files;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class XmlFileHandler {

    final static XmlMapper xmlMapper = (XmlMapper) new XmlMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static <T> T load(File file, Class<T> type) throws IOException {
        return xmlMapper.readValue(file, type);
    }

    public static void save(File file, Object object) throws IOException {
        final Writer output = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        xmlMapper.writeValue(output, object);
        output.close();
    }
}
