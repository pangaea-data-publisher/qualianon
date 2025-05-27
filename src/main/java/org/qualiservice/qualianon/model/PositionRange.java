package org.qualiservice.qualianon.model;

import javafx.scene.control.IndexRange;

import java.util.Objects;


public class PositionRange {

    private final int start;
    private final int end;

    public PositionRange(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        this.start = start;
        this.end = end;
    }

    public PositionRange(IndexRange indexRange) {
        this(indexRange.getStart(), indexRange.getEnd());
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isSameStartAndEndInside(PositionRange other) {
        return start == other.start && end >= other.end;
    }

    public boolean isSameEndAndStartInside(PositionRange other) {
        return start <= other.start && end == other.end;
    }

    public boolean isInside(PositionRange other) {
        return start <= other.start && end >= other.end;
    }

    public boolean isSameStart(PositionRange other) {
        return start == other.start;
    }

    public boolean isSameEnd(PositionRange other) {
        return end == other.end;
    }

    public boolean isStartInside(PositionRange other) {
        return start <= other.start && end >= other.start;
    }

    public boolean isEndInside(PositionRange other) {
        return start <= other.end && end >= other.end;
    }

    public boolean overlaps(PositionRange other) {
        return isStartInside(other)
                || isEndInside(other)
                || other.isStartInside(this)
                || other.isEndInside(this);
    }

    public int length() {
        return end - start;
    }

    public IndexRange asIndexRange() {
        return new IndexRange(start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionRange that = (PositionRange) o;
        return start == that.start &&
                end == that.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "PositionRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
