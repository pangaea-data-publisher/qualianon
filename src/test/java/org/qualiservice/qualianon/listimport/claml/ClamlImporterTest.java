package org.qualiservice.qualianon.listimport.claml;

import org.junit.Test;
import org.qualiservice.qualianon.model.categories.CodingList;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.qualiservice.qualianon.test.Fixture.fixtureAsFile;


public class ClamlImporterTest {

    @Test
    public void deserializeFromXmlTest() throws IOException {
        final File file = fixtureAsFile("fixtures/claml-example.xml");
        final CodingList codingList = ClamlImporter.read(file);

        final String source = "claml-example";
        final List<String> header = Arrays.asList("Level 1", "Level 2", "Level 3");
        final List<List<String>> data = Arrays.asList(
                Arrays.asList("Text 1", "Text 2", "Text 3"),
                Arrays.asList("Text 1", "Text 4")
        );
        final CodingList expected = new CodingList(source, header, data);

        assertEquals(expected, codingList);
    }

    @Test(expected = IOException.class)
    public void deserializeWrongFormatTest() throws IOException {
        final File file = fixtureAsFile("fixtures/claset-example.xml");
        ClamlImporter.read(file);
    }

}
