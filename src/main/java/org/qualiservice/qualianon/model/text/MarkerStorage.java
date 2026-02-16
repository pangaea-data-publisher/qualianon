package org.qualiservice.qualianon.model.text;

import java.beans.Transient;
import java.util.Objects;
import java.util.UUID;


public class MarkerStorage {

    private UUID id;
    private String original;
    private String note;
    private UUID replacementId;

    public UUID getId() {
        return id;
    }

    public MarkerStorage setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getOriginal() {
        return original;
    }

    public MarkerStorage setOriginal(String original) {
        this.original = original;
        return this;
    }

    public String getNote() {
        return note;
    }

    public MarkerStorage setNote(String note) {
        this.note = note;
        return this;
    }

    public UUID getReplacementId() {
        return replacementId;
    }

    public MarkerStorage setReplacementId(UUID replacementId) {
        this.replacementId = replacementId;
        return this;
    }

    /**
     * Returns the placeholder code used inside anonymized text for this marker.
     */
    @Transient
    public String getCode() {
        return Codes.marker(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkerStorage that = (MarkerStorage) o;
        return Objects.equals(id, that.id) && Objects.equals(original, that.original) && Objects.equals(note, that.note) && Objects.equals(replacementId, that.replacementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, original, note, replacementId);
    }

    @Override
    public String toString() {
        return "MarkerStorage{" +
                "id=" + id +
                ", original='" + original + '\'' +
                ", note='" + note + '\'' +
                ", replacementId=" + replacementId +
                '}';
    }
}
