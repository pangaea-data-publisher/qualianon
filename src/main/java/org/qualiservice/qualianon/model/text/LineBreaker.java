package org.qualiservice.qualianon.model.text;

import org.qualiservice.qualianon.utility.UserPreferences;

public class LineBreaker {

    public static String formatDocument(String text, int lineLength, boolean breakLine){
        if (breakLine){
           return formatDocumentOptional(text,lineLength);
        } else {
            return text;
        }
    }
    private static String formatDocumentOptional(String text, int lineLength) {
        final StringBuilder sb = new StringBuilder();
        final String[] lines = text.split("\n");
        for (final String line : lines) {
            formatLine(line, lineLength, sb);
            sb.append('\n');
        }
        return sb.toString();
    }

    private static void formatLine(String line, int lineLength, StringBuilder sb) {
        final String[] words = line.split(" ");
        int charCount = 0;
        for (final String word : words) {
            if (charCount + word.length() > lineLength && charCount > 0) {
                sb.append('\n');
                charCount = 0;
            }
            if (charCount > 0) {
                sb.append(' ');
                charCount++;
            }
            sb.append(word);
            charCount += word.length();
        }
    }

}
