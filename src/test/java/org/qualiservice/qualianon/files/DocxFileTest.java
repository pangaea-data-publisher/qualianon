package org.qualiservice.qualianon.files;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class DocxFileTest {

    @Test
    public void readDocumentTest() throws IOException {
        final File file = new File("src/test/resources/fixtures/interview.docx");
        final String text = DocxFile.read(file);
        assertEquals("Hallo\nWölt!\nNew paragraph\n", text);
    }

    @Test
    public void writeDocumentTest() throws IOException {
        final File file = new File("src/test/resources/fixtures/interview-out.docx");
        final String text = "Hallo meine\nliebe Welt";
        DocxFile.write(file, text);
    }

}
