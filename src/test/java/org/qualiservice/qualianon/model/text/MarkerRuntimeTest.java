package org.qualiservice.qualianon.model.text;

import org.junit.Before;
import org.junit.Test;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.anonymization.CategoryProfile;
import org.qualiservice.qualianon.model.anonymization.LabelProfile;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.Label;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class MarkerRuntimeTest {

    private final MessageLogger ml = mock(MessageLogger.class);
    private MarkerRuntime markerRuntime;

    @Before
    public void setup() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml)
                .addLabel(new Label("gender", "diverse"));
        markerRuntime = new MarkerRuntime(new PositionRange(1, 10), replacement);
    }

    @Test
    public void getExportCategoryNotPresentTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile();
        assertEquals("[Person]", markerRuntime.getExport(anonymizationProfile, new ReplacementCounter()));
    }

    @Test
    public void getExportWithCategoryTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person"));
        assertEquals("[Person]", markerRuntime.getExport(anonymizationProfile, new ReplacementCounter()));
    }

    @Test
    public void getExportWithLabelButNotInProfileTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person"));
        assertEquals("[Person]", markerRuntime.getExport(anonymizationProfile, new ReplacementCounter()));
    }

    @Test
    public void getExportWithLabelAndInProfileTest() {
        assertEquals("[Person | gender: diverse]", markerRuntime.getExport(getAnonymizationProfile(), new ReplacementCounter()));
    }

    @Test
    public void getExportWithLabelEnabledButEmpty() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml)
                .addLabel(new Label("gender", ""));
        final MarkerRuntime markerRuntime = new MarkerRuntime(new PositionRange(1, 10), replacement);
        assertEquals("[Person]", markerRuntime.getExport(getAnonymizationProfile(), new ReplacementCounter()));
    }

    @Test
    public void getExportWithCountingTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person")
                        .setCountingEnabled(true)
                );
        assertEquals("[Person 1]", markerRuntime.getExport(anonymizationProfile, new ReplacementCounter()));
    }

    private AnonymizationProfile getAnonymizationProfile() {
        return new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person")
                        .addLabelProfile(new LabelProfile("gender", true))
                );
    }
}
