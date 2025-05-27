package org.qualiservice.qualianon.model.text;

import org.apache.commons.lang3.StringUtils;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;
import org.qualiservice.qualianon.model.project.SearchParams;

import java.util.*;
import java.util.stream.Collectors;


public class IndexedText {

    private String text;
    private String textLowerCase;
    private List<MarkerRuntime> markers;
    private LineIndex lineIndex;

    public IndexedText(String text) {
        markers = new LinkedList<>();
        setText(text);
    }

    public void setText(String text) {
        this.text = text;
        textLowerCase = text.toLowerCase(Locale.getDefault());
        lineIndex = new LineIndex(text);
    }

    public String getText() {
        return text;
    }

    public LineIndex getLineIndex() {
        return lineIndex;
    }

    public List<MarkerRuntime> getMarkers() {
        return markers;
    }

    public IndexedText setMarkers(List<MarkerRuntime> markers) {
        this.markers = markers;
        return this;
    }

    public void addMarker(MarkerRuntime marker) {
        markers.add(marker);
        markers.sort(Comparator.comparingInt(markerComp -> markerComp.getPositionRange().getStart()));
    }

    public void removeMarker(MarkerRuntime marker) {
        markers.remove(marker);
    }

    public String toAnonymized() {
        return anonymize(MarkerRuntime::getCode);
    }

    public String toExport(AnonymizationProfile anonymizationProfile, boolean withLineNumbers) {
        final ReplacementCounter replacementCounter = new ReplacementCounter();
        final String anonymized = anonymize(marker -> {
            if (anonymizationProfile.getCategoryProfile(marker.getReplacement().getCategoryScheme()).isOriginalEnabled()) {
                return getTextInRange(marker.getPositionRange());
            }
            return marker.getExport(anonymizationProfile, replacementCounter);
        });
        if (withLineNumbers) {
            return addLineNumbers(anonymized);
        }
        return anonymized;
    }

    private String anonymize(Anonymizer anonymizer) {
        final StringBuilder sb = new StringBuilder();
        int textIndex = 0;
        for (final MarkerRuntime marker : markers) {
            sb.append(text, textIndex, marker.getPositionRange().getStart());
            sb.append(anonymizer.getAnonymizedText(marker));
            final String original = text.substring(marker.getPositionRange().getStart(), marker.getPositionRange().getEnd());
            final int newlineCount = StringUtils.countMatches(original, "\n");
            sb.append(StringUtils.repeat("\n", newlineCount));
            textIndex = marker.getPositionRange().getEnd();
        }
        sb.append(text.substring(textIndex));
        return sb.toString();
    }

    public static IndexedText fromAnonymized(String anonymized, List<MarkerStorage> markers, ReplacementCollection replacementCollection, MessageLogger messageLogger) {
        final List<MarkerRuntime> markerRuntimes = new LinkedList<>();
        String text = anonymized;

        for (final MarkerStorage marker : markers) {
            final int index = text.indexOf(marker.getCode());
            if (index == -1) continue;

            final String original = Optional.ofNullable(marker.getOriginal()).orElse("<removed>");
            text = text.replace(marker.getCode(), original);
            final int endIndex = index + original.length();
            final Replacement replacement = replacementCollection.getById(marker.getReplacementId());
            if (replacement == null) {
                messageLogger.logError("Cannot find replacement for marked original \"" + marker.getOriginal() + "\", marker_ID=" + marker.getId() + ", replacement_ID=" + marker.getReplacementId(), null);
                continue;
            }

            final MarkerRuntime markerRuntime = new MarkerRuntime(
                    new PositionRange(index, endIndex),
                    replacement
            )
                    .setId(marker.getId());
            markerRuntimes.add(markerRuntime);

            final int newlineCount = StringUtils.countMatches(marker.getOriginal(), '\n');

            // Remove placeholder newlines in multiline-originals
            int beginIndex = endIndex + newlineCount;
            if (beginIndex < text.length()) {
                text = text.substring(0, endIndex) + text.substring(beginIndex);
            }
        }

        return new IndexedText(text).setMarkers(markerRuntimes);
    }

    public List<SearchResult> search(SearchParams searchParams, String documentName) {
        final LinkedList<SearchResult> results = new LinkedList<>();
        int fromIndex = 0;
        final String searchString = searchParams.getSearchString();
        final String textBody = getTextBodyForCase(searchParams.isMatchCase());

        while (true) {
            final int i = textBody.indexOf(searchString, fromIndex);
            if (i == -1) break;

            fromIndex = i + 1;
            final PositionRange range = new PositionRange(i, i + searchParams.getText().length());
            if (searchParams.isUnmarked() && countMarkersInRange(range) > 0) continue;
            if (searchParams.isWholeWords() && !checkIsWholeWords(range)) continue;

            final Coords coords = lineIndex.getCoordsAt(i);
            final SearchResult searchResult = new SearchResult(
                    documentName, coords, lineIndex.getLine(coords.getLine(), text), range
            );
            results.add(searchResult);
        }
        return results;
    }

    private String getTextBodyForCase(boolean matchCase) {
        if (matchCase) return text;
        return textLowerCase;
    }

    private boolean checkIsWholeWords(PositionRange range) {
        if (range.getStart() > 0) {
            final char charBefore = text.charAt(range.getStart() - 1);
            if (Character.isLetterOrDigit(charBefore)) return false;
        }
        if (range.getEnd() < text.length()) {
            final char charAfter = text.charAt(range.getEnd());
            //noinspection RedundantIfStatement
            if (Character.isLetterOrDigit(charAfter)) return false;
        }
        return true;
    }

    public int countMarkersInRange(PositionRange range) {
        return (int) markers.stream()
                .filter(markerRuntime -> markerRuntime.getPositionRange().overlaps(range))
                .count();
    }

    public MarkerRuntime getMarkerInSelection(PositionRange range) {
        return markers.stream()
                .filter(markerRuntime -> markerRuntime.getPositionRange().overlaps(range))
                .findAny()
                .orElse(null);
    }

    public String getTextInRange(PositionRange range) {
        return text.substring(range.getStart(), range.getEnd());
    }

    public List<MarkerStorage> getMarkersForStorage() {
        return markers.stream()
                .map(markerRuntime -> new MarkerStorage()
                        .setId(markerRuntime.getId())
                        .setNote(markerRuntime.getNote())
                        .setReplacementId(markerRuntime.getReplacement().getId())
                        .setOriginal(text.substring(markerRuntime.getPositionRange().getStart(), markerRuntime.getPositionRange().getEnd()))
                )
                .collect(Collectors.toList());
    }

    public static String addLineNumbers(String text) {
        final StringBuilder sb = new StringBuilder();
        final String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            sb.append("(").append(i + 1).append(")   ").append(lines[i]).append("\n");
        }
        return sb.toString();
    }

    private interface Anonymizer {
        String getAnonymizedText(MarkerRuntime marker);
    }
}

