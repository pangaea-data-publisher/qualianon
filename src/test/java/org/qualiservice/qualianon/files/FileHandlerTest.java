package org.qualiservice.qualianon.files;

import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;


public class FileHandlerTest {

    @Test
    public void withFileExtensionNoneTest() {
        final File file = new File("hallo");
        final File withExtension = FileHandler.withFileExtension(file, "xml");
        assertEquals("hallo.xml", withExtension.getName());
    }

    @Test
    public void withFileExtensionOtherTest() {
        final File file = new File("hallo.txt");
        final File withExtension = FileHandler.withFileExtension(file, "xml");
        assertEquals("hallo.txt.xml", withExtension.getName());
    }

}
