package org.qualiservice.qualianon.model.text;

import java.util.Objects;

public class Coords {

    private final int line;
    private final int column;

    public Coords(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coords coords = (Coords) o;
        return line == coords.line && column == coords.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column);
    }

    @Override
    public String toString() {
        return "Coords{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }
}
