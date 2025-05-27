package org.qualiservice.qualianon.model.project;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;


public class AnonymizedFileTest {

    @Test
    public void getMarkerStorageFileTest() {
        final AnonymizedFile anonymizedFile = new AnonymizedFile(
                null,
                new File("/my/project/identification"),
                new File("/my/project/trash"),
                null
        );
        anonymizedFile.setFile(new File("/this/is/path/filename.docx"));
        final String absolutePath = anonymizedFile.getMarkerStorageFile().getAbsolutePath();
        System.out.println(absolutePath);
        assertTrue(absolutePath.equals("/my/project/identification/filename_markers.xlsx") || absolutePath.endsWith(":\\my\\project\\identification\\filename_markers.xlsx"));
    }

}
