package org.qualiservice.qualianon.model.text;

import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.anonymization.CategoryProfile;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.Label;
import org.qualiservice.qualianon.utility.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.qualiservice.qualianon.utility.ExtStream.optionalStream;


public class MarkerRuntime {

    public static int CHARACTER_LIMIT = 32000;
    private UUID id;
    private PositionRange positionRange;
    private Replacement replacement;
    private String note;

    public MarkerRuntime(PositionRange positionRange, Replacement replacement) {
        validateSelectionRange(positionRange);
        this.positionRange = positionRange;
        this.replacement = replacement;
        id = UUID.randomUUID();
    }

    public static void validateSelectionRange(PositionRange positionRange) {
        if (positionRange.length() > CHARACTER_LIMIT) {
            throw new RuntimeException("Marker is too long (" + positionRange.length() + " characters). Limit is " + CHARACTER_LIMIT);
        }
    }

    public UUID getId() {
        return id;
    }

    public MarkerRuntime setId(UUID id) {
        this.id = id;
        return this;
    }

    public PositionRange getPositionRange() {
        return positionRange;
    }

    public void setPositionRange(PositionRange positionRange) {
        validateSelectionRange(positionRange);
        this.positionRange = positionRange;
    }

    public Replacement getReplacement() {
        return replacement;
    }

    public void setReplacement(Replacement replacement) {
        this.replacement = replacement;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCode() {
        return Codes.marker(id);
    }

    public String getExport(AnonymizationProfile anonymizationProfile, ReplacementCounter replacementCounter) {
        final CategoryProfile categoryProfile = anonymizationProfile.getCategoryProfile(
                replacement.getCategoryScheme()
        );

        final Optional<String> categoryOptional = Stream.concat(
                        optionalStream(Optional.of(categoryProfile.getCategoryName())),
                        optionalStream(getCountingOptional(categoryProfile, replacement, replacementCounter))
                )
                .reduce((s1, s2) -> s1 + " " + s2);

        final Stream<String> labelsStream = replacement.getLabels().stream()
                .filter(label -> anonymizationProfile.isLabelEnabled(replacement.getCategoryScheme(), label.getLevel()))
                .filter(label -> !StringUtils.isBlank(label.getValue()))
                .map(Label::getExportText);

        final String exportText = Stream.concat(
                        optionalStream(categoryOptional),
                        labelsStream
                )
                .reduce((s1, s2) -> s1 + " | " + s2)
                .orElse("?");

        return "[" + exportText + "]";
    }

    private Optional<String> getCountingOptional(CategoryProfile categoryProfile, Replacement replacement, ReplacementCounter replacementCounter) {
        if (!categoryProfile.isCountingEnabled()) {
            return Optional.empty();
        }
        return Optional.of(String.valueOf(replacementCounter.get(replacement)));
    }

    @Override
    public String toString() {
        return "MarkerRuntime{" +
                "id=" + id +
                ", positionRange=" + positionRange +
                ", replacement=" + replacement +
                ", note='" + note + '\'' +
                '}';
    }
}
