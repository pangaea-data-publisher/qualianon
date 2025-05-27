package org.qualiservice.qualianon.model.text;

import java.util.LinkedList;


public class LineIndex {

    private final LinkedList<Integer> lineToPosition;

    public LineIndex(String text) {
        lineToPosition = new LinkedList<>();
        lineToPosition.add(-1); // The first newline would be before the first character at pos 0

        int newlinePosition = 0;
        while (true) {
            final int i = text.indexOf("\n", newlinePosition);
            if (i == -1) break;
            lineToPosition.add(i);
            newlinePosition = i + 1;
        }
    }

    public Coords getCoordsAt(int position) {
        int lineNumber = 0;
        for (final Integer newlinePosition : lineToPosition) {
            if (position < newlinePosition) {
                return new Coords(lineNumber, newlinePosition - position + 2);
            }
            lineNumber++;
        }
        return new Coords(lineNumber, position - lineToPosition.getLast());
    }

    public String getLine(int line, String text) {
        final int startPos = lineToPosition.get(line - 1) + 1;
        if (line >= lineToPosition.size()) {
            return text.substring(startPos);
        }
        final Integer endPos = lineToPosition.get(line);
        return text.substring(startPos, endPos);
    }

}
