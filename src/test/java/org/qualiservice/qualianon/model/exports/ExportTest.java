package org.qualiservice.qualianon.model.exports;

import org.junit.Test;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.anonymization.CategoryProfile;
import org.qualiservice.qualianon.model.text.IndexedText;
import org.qualiservice.qualianon.utility.UserPreferences;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

public class ExportTest {

    private final MessageLogger messageLogger = mock(MessageLogger.class);

    @Test
    public void isEqualTest() {
        assertEquals(
                new Export(new File("dir1"), new File("trash1"), messageLogger)
                        .setName("Name")
                        .setAnonymizationProfile(new AnonymizationProfile().addCategoryProfile(new CategoryProfile("Person"))),
                new Export(new File("dir2"), new File("trash2"), messageLogger)
                        .setName("Name")
                        .setAnonymizationProfile(new AnonymizationProfile().addCategoryProfile(new CategoryProfile("Location")))
        );
    }

    @Test
    public void isNotEqualTest() {
        assertNotEquals(
                new Export(new File("dir"), new File("trash"), messageLogger)
                        .setName("Name1")
                        .setAnonymizationProfile(new AnonymizationProfile().addCategoryProfile(new CategoryProfile("Person"))),
                new Export(new File("dir"), new File("trash"), messageLogger)
                        .setName("Name2")
                        .setAnonymizationProfile(new AnonymizationProfile().addCategoryProfile(new CategoryProfile("Person")))
        );
    }


}
