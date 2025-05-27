package org.qualiservice.qualianon.listimport.sgtyp;

import org.junit.Test;
import org.qualiservice.qualianon.model.categories.CodingList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.qualiservice.qualianon.test.Fixture.fixtureAsFile;


public class SgtypImporterTest {

    @Test
    public void importTest() throws IOException {
        final CodingList codingList = SgtypImporter.read(fixtureAsFile("fixtures/sgtyp-example.xlsx"));

        final String source = "Referenzdatei: Stadt- und Gemeindetyp, BBSR Bonn 2017";
        final List<String> header = Arrays.asList("Bundesland", "Typ einfach", "Typ differenziert", "Gemeinde");
        final List<List<String>> data = Arrays.asList(
                Arrays.asList("Schleswig-Holstein", "Mittelstadt", "Größere Mittelstadt", "Flensburg, Stadt"),
                Arrays.asList("Schleswig-Holstein", "Großstadt", "Kleinere Großstadt", "Kiel, Landeshauptstadt")
        );
        final CodingList expected = new CodingList(source, header, data);

        assertEquals(expected, codingList);
    }

}
