package org.qualiservice.qualianon.gui.components.documentview;

import javafx.scene.control.IndexRange;
import org.qualiservice.qualianon.utility.StringUtils;

public class SelectionManager {

    public static IndexRange extendSelection(String text, IndexRange selection) {

        if (StringUtils.isBlank(text)) return selection;

        final IndexRange bracketsRange = extendToSquareBrackets(text, selection);
        if (bracketsRange != null) {
            return bracketsRange;
        }

        int startPos = selection.getStart();
        for (; startPos > 0; startPos--) {
            if (invalidCharacter(text.charAt(startPos - 1))) break;
        }

        int endPos = selection.getEnd();
        if (selection.getLength() > 0 && invalidCharacter(text.charAt(selection.getEnd() - 1))) {
            endPos--;
        }
        for (; endPos < text.length(); endPos++) {
            if (invalidCharacter(text.charAt(endPos))) break;
        }

        return new IndexRange(startPos, endPos);
    }

    public static IndexRange extendSelection(String text, int position) {
        return extendSelection(text, new IndexRange(position, position));
    }

    private static boolean invalidCharacter(char c) {
        return !Character.isLetterOrDigit(c);
    }

    private static IndexRange extendToSquareBrackets(String text, IndexRange selection) {
        if (text.length() <= selection.getStart()) return selection;

        final String selectedText = text.substring(selection.getStart(), selection.getEnd()).trim();
        if (selectedText.startsWith("[") && selectedText.endsWith("]")) {
            return new IndexRange(
                    text.indexOf('[', selection.getStart()),
                    text.lastIndexOf(']', selection.getEnd() - 1) + 1
            );
        }

        int startMod = -1;
        int endMod = 0;
        if (text.charAt(selection.getStart()) == '[') {
            startMod = 0;
            endMod = 1;
        }
        int rightClose = text.indexOf(']', selection.getEnd() + endMod);
        int rightOpen = text.indexOf('[', selection.getEnd() + endMod);
        int leftOpen = text.lastIndexOf('[', selection.getStart() + startMod);
        int leftClose = text.lastIndexOf(']', selection.getStart() + startMod);

        if (rightClose == -1 || leftOpen == -1) return null;
        if (rightOpen != -1 && rightOpen < rightClose) return null;
        if (leftClose != -1 && leftOpen < leftClose) return null;

        return new IndexRange(leftOpen, rightClose + 1);
    }

    public static boolean rangeCrossesMarking(String text) {
        if (text.startsWith("[") && text.endsWith("]")) {
            text = text.substring(1, text.length() - 1);
        }
        return text.contains("[") || text.contains("]");
    }
}
