package org.qualiservice.qualianon.model.anonymization;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.test.Fixture;

import java.io.IOException;

import static org.junit.Assert.*;


public class AnonymizationProfileTest {

    @Test
    public void serializeToXmlTest() throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        final String xmlString = xmlMapper.writeValueAsString(getTestProfile());
        assertEquals(Fixture.fixture("fixtures/anonymization_profile.xml"), xmlString);
    }

    @Test
    public void deserializeFromXmlTest() throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        final AnonymizationProfile anonymizationProfile = xmlMapper.readValue(Fixture.fixture("fixtures/anonymization_profile.xml"), AnonymizationProfile.class);
        assertEquals(getTestProfile(), anonymizationProfile);
    }

    @Test
    public void isLabelEnabledNotInProfileTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile();
        assertFalse(anonymizationProfile.isLabelEnabled(new CategoryScheme("Person"), "Gender"));
    }

    @Test
    public void isLabelEnabledTrueTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person")
                        .addLabelProfile(new LabelProfile("Gender", true))
                );
        assertTrue(anonymizationProfile.isLabelEnabled(new CategoryScheme("Person"), "Gender"));
    }

    @Test
    public void isLabelEnabledFalseTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person")
                        .addLabelProfile(new LabelProfile("Gender", false))
                );
        assertFalse(anonymizationProfile.isLabelEnabled(new CategoryScheme("Person"), "Gender"));
    }

    private AnonymizationProfile getTestProfile() {
        return new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person")
                        .setCountingEnabled(true)
                        .addLabelProfile(new LabelProfile("Gender", true))
                )
                .addCategoryProfile(new CategoryProfile("Location")
                );
    }
}
