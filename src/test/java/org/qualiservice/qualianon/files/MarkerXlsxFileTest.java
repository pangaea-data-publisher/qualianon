package org.qualiservice.qualianon.files;

import org.junit.Test;
import org.qualiservice.qualianon.model.text.MarkerStorage;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;


public class MarkerXlsxFileTest {

    @Test
    public void writeReadTest() throws IOException {
        final MarkerStorage markerStorage = new MarkerStorage()
                .setId(UUID.fromString("9e30ac9a-ac1f-45a6-8beb-d88a2b293bac"))
                .setReplacementId(UUID.fromString("d365ce1d-a5b1-4f5f-a1bd-d5e75e2eb453"))
                .setOriginal("Original here")
                .setNote("Some note");
        final File file = new File("src/test/resources/fixtures/markers-out.xlsx");

        MarkerXlsxFile.write(file, Collections.singletonList(markerStorage));
        final List<MarkerStorage> loadedMarkers = MarkerXlsxFile.read(file);
        assertEquals(Collections.singletonList(markerStorage), loadedMarkers);

        file.delete();
    }

}
