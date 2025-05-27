package org.qualiservice.qualianon.model.text;

import org.junit.Test;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.anonymization.CategoryProfile;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;
import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.project.SearchParams;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class IndexedTextTest {

    private final MessageLogger ml = mock(MessageLogger.class);

    @Test
    public void noMarkerTest() {
        final IndexedText indexedText = new IndexedText("Hallo Welt!");
        assertEquals("Hallo Welt!", indexedText.toAnonymized());
    }

    @Test
    public void oneMarkerTest() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final MarkerRuntime marker = new MarkerRuntime(new PositionRange(6, 10), replacement);
        final IndexedText indexedText = new IndexedText("Hallo Welt!");
        indexedText.addMarker(marker);

        final String anonymized = indexedText.toAnonymized();
        assertEquals("Hallo " + marker.getCode() + "!", anonymized);
    }

    @Test
    public void markerForStorageTest() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final MarkerRuntime marker = new MarkerRuntime(new PositionRange(6, 10), replacement);
        final IndexedText indexedText = new IndexedText("Hallo Welt!");
        indexedText.addMarker(marker);

        final List<MarkerStorage> markers = indexedText.getMarkersForStorage();

        assertEquals(1, markers.size());
        final MarkerStorage expected = new MarkerStorage()
                .setId(marker.getId())
                .setOriginal("Welt")
                .setNote(null)
                .setReplacementId(replacement.getId());
        assertEquals(expected, markers.get(0));
    }

    @Test
    public void sortMarkersTest() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final MarkerRuntime marker1 = new MarkerRuntime(new PositionRange(6, 10), replacement);
        final MarkerRuntime marker2 = new MarkerRuntime(new PositionRange(0, 5), replacement);
        final IndexedText indexedText = new IndexedText("Hallo Welt!");
        indexedText.addMarker(marker1);
        indexedText.addMarker(marker2);

        final String anonymized = indexedText.toAnonymized();
        assertEquals(marker2.getCode() + " " + marker1.getCode() + "!", anonymized);
    }

    @Test
    public void multilineMarkerTest() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final MarkerRuntime marker = new MarkerRuntime(new PositionRange(6, 17), replacement);
        final IndexedText indexedText = new IndexedText("Hallo meine\nliebe Welt!");
        indexedText.addMarker(marker);

        final String anonymized = indexedText.toAnonymized();
        assertEquals("Hallo " + marker.getCode() + "\n Welt!", anonymized);
    }

    @Test
    public void fromAnonymizedSimple() {
        final IndexedText indexedText = IndexedText.fromAnonymized("Hallo Welt!", Collections.emptyList(), null, null);
        assertEquals("Hallo Welt!", indexedText.getText());
    }

    @Test
    public void fromAnonymizedWithMarker() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final MarkerStorage marker = new MarkerStorage()
                .setId(UUID.randomUUID())
                .setOriginal("Welt")
                .setReplacementId(replacement.getId());
        final ReplacementCollection replacementCollection = new ReplacementCollection().add(replacement);

        final IndexedText indexedText = IndexedText.fromAnonymized(
                "Hallo " + marker.getCode() + "!",
                Collections.singletonList(marker),
                replacementCollection,
                null
        );

        assertEquals("Hallo Welt!", indexedText.getText());
    }

    @Test
    public void fromAnonymizedWithMarkerRemoved() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final MarkerStorage marker = new MarkerStorage()
                .setId(UUID.randomUUID())
                .setReplacementId(replacement.getId());
        final ReplacementCollection replacementCollection = new ReplacementCollection().add(replacement);

        final IndexedText indexedText = IndexedText.fromAnonymized(
                "Hallo " + marker.getCode() + "!",
                Collections.singletonList(marker),
                replacementCollection,
                null
        );

        assertEquals("Hallo <removed>!", indexedText.getText());
    }

    @Test
    public void fromAnonymizedMultilineWithMarker() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final MarkerStorage marker = new MarkerStorage()
                .setId(UUID.randomUUID())
                .setOriginal("meine\nliebe")
                .setReplacementId(replacement.getId());
        final ReplacementCollection replacementCollection = new ReplacementCollection().add(replacement);

        final IndexedText indexedText = IndexedText.fromAnonymized(
                "Hallo " + marker.getCode() + "\n Welt!",
                Collections.singletonList(marker),
                replacementCollection,
                null
        );

        assertEquals("Hallo meine\nliebe Welt!", indexedText.getText());
    }

    @Test
    public void fromToAnonymized() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final MarkerStorage marker = new MarkerStorage()
                .setId(UUID.randomUUID())
                .setOriginal("Welt")
                .setReplacementId(replacement.getId());
        final ReplacementCollection replacementCollection = new ReplacementCollection().add(replacement);

        final String anonymized = "Hallo " + marker.getCode() + "!";
        final IndexedText indexedText = IndexedText.fromAnonymized(anonymized, Collections.singletonList(marker), replacementCollection, null);
        assertEquals(anonymized, indexedText.toAnonymized());
    }

    @Test
    public void searchNoFiltersTest() {
        final IndexedText indexedText = new IndexedText("Hallo Welt!");
        final List<SearchResult> searchResults = indexedText.search(SearchParams.plain("welt"), "file.docx");
        assertEquals(1, searchResults.size());
        assertEquals(new SearchResult("file.docx", new Coords(1, 7), "Hallo Welt!", new PositionRange(6, 10)), searchResults.get(0));
    }

    @Test
    public void searchMatchCaseTest() {
        final String text = "Hallo Welt welt!";
        final IndexedText indexedText = new IndexedText(text);
        final List<SearchResult> searchResults = indexedText.search(SearchParams.plain("Welt").withMatchCase(), "file.docx");
        assertEquals(1, searchResults.size());
        assertEquals(new SearchResult("file.docx", new Coords(1, 7), text, new PositionRange(6, 10)), searchResults.get(0));
    }

    @Test
    public void searchMultiTest() {
        final IndexedText indexedText = new IndexedText("Hallo Welt Welt!");
        final List<SearchResult> searchResults = indexedText.search(SearchParams.plain("Welt"), "file.docx");
        assertEquals(2, searchResults.size());
        assertEquals(new SearchResult("file.docx", new Coords(1, 7), "Hallo Welt Welt!", new PositionRange(6, 10)), searchResults.get(0));
        assertEquals(new SearchResult("file.docx", new Coords(1, 12), "Hallo Welt Welt!", new PositionRange(11, 15)), searchResults.get(1));
    }

    @Test
    public void searchLineTest() {
        final IndexedText indexedText = new IndexedText("Hallo\nmeine Welt!");
        final List<SearchResult> searchResults = indexedText.search(SearchParams.plain("Welt"), "file.docx");
        assertEquals(1, searchResults.size());
        assertEquals(new SearchResult("file.docx", new Coords(2, 7), "meine Welt!", new PositionRange(12, 16)), searchResults.get(0));
    }

    @Test
    public void searchWholeWordsTest() {
        final String text = "Welti Hallo -Welt, Hall oWelt! Welta AWelt";
        final IndexedText indexedText = new IndexedText(text);
        final List<SearchResult> searchResults = indexedText.search(SearchParams.plain("Welt").withWholeWords(), "file.docx");
        assertEquals(1, searchResults.size());
        assertEquals(new SearchResult("file.docx", new Coords(1, 14), text, new PositionRange(13, 17)), searchResults.get(0));
    }

    @Test
    public void searchUnmarkedTest() {
        final String text = "Hallo Welt, Welt";
        final IndexedText indexedText = new IndexedText(text);
        indexedText.addMarker(new MarkerRuntime(new PositionRange(12, 16), new Replacement(new CategoryScheme("Test"), ml)));
        final List<SearchResult> searchResults = indexedText.search(SearchParams.plain("Welt").withUnmarked(), "file.docx");
        assertEquals(1, searchResults.size());
        assertEquals(new SearchResult("file.docx", new Coords(1, 7), text, new PositionRange(6, 10)), searchResults.get(0));
    }

    @Test
    public void searchNotUnmarkedTest() {
        final String text = "Hallo Welt, Welt";
        final IndexedText indexedText = new IndexedText(text);
        indexedText.addMarker(new MarkerRuntime(new PositionRange(12, 16), new Replacement(new CategoryScheme("Test"), ml)));
        final List<SearchResult> searchResults = indexedText.search(SearchParams.plain("Welt"), "file.docx");
        assertEquals(2, searchResults.size());
        assertEquals(new SearchResult("file.docx", new Coords(1, 7), text, new PositionRange(6, 10)), searchResults.get(0));
        assertEquals(new SearchResult("file.docx", new Coords(1, 13), text, new PositionRange(12, 16)), searchResults.get(1));
    }

    @Test
    public void exportWithCategoryTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person")
                );
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final IndexedText indexedText = new IndexedText("Hallo Welt!");
        indexedText.addMarker(new MarkerRuntime(new PositionRange(6, 10), replacement));
        final String export = indexedText.toExport(anonymizationProfile, false);
        assertEquals("Hallo [Person]!", export);
    }

    @Test
    public void exportWithOriginalTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile()
                .addCategoryProfile(new CategoryProfile("Person")
                        .setOriginalEnabled(true)
                );
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        final IndexedText indexedText = new IndexedText("Hallo Welt!");
        indexedText.addMarker(new MarkerRuntime(new PositionRange(6, 10), replacement));
        final String export = indexedText.toExport(anonymizationProfile, false);
        assertEquals("Hallo Welt!", export);
    }

    @Test
    public void exportWithLinenumbersTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile();
        final IndexedText indexedText = new IndexedText("Hallo Welt!\nZeile 2");
        final boolean withLineNumber = true;
        final String export = indexedText.toExport(anonymizationProfile, withLineNumber);
        assertEquals("(1)   Hallo Welt!\n(2)   Zeile 2\n", export);
    }
    @Test
    public void exportWithoutLinenumbersTest() {
        final AnonymizationProfile anonymizationProfile = new AnonymizationProfile();
        final IndexedText indexedText = new IndexedText("Hallo Welt!\nZeile 2");
        final boolean withLineNumber = false;
        final String export = indexedText.toExport(anonymizationProfile, withLineNumber);
        assertEquals(indexedText.getText(), export);
    }

    @Test
    public void addLineNumbersTest() {
        final String original = "Hallo Welt!\nZeile 2";
        assertEquals("(1)   Hallo Welt!\n(2)   Zeile 2\n", IndexedText.addLineNumbers(original));
    }

}
