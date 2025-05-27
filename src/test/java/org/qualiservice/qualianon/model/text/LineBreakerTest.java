package org.qualiservice.qualianon.model.text;

import org.junit.Test;
import org.qualiservice.qualianon.model.text.LineBreaker;
import org.qualiservice.qualianon.utility.UserPreferences;

import static org.junit.Assert.assertEquals;

public class LineBreakerTest {
    final String unbrokenText = "\n" +
            "Dass [Person | Geschlecht: Weiblich | Rolle: Boss | Abteilung/Schwerpunkt: Fantasienreporter] letzte Monat noch da war, dass es erst die erste von\n" +
            "dreizig Wochen ist. \n" +
            "A: Genau, letzte Monat noch nicht da war. Wie\n" +
            "schnell ging das? #00:00:35-3#\n" +
            "A: So, und du bist diese Woche allein im Mondressort, \n";
    final String targetBrokenText = "\n" +
            "Dass [Person | Geschlecht: Weiblich | Rolle: Boss |\n" +
            "Abteilung/Schwerpunkt: Fantasienreporter] letzte Monat noch da war,\n" +
            "dass es erst die erste von\n" +
            "dreizig Wochen ist.\n" +
            "A: Genau, letzte Monat noch nicht da war. Wie\n" +
            "schnell ging das? #00:00:35-3#\n" +
            "A: So, und du bist diese Woche allein im Mondressort,\n";  //since each line is < 70 chars

    @Test
    public void breakLongLineTest() {
        final int linelength = 70;
        final boolean breakLine = true;
        final String actualBrokenText = LineBreaker.formatDocument(unbrokenText, linelength,breakLine);
        assertEquals(actualBrokenText,targetBrokenText);
    }
    @Test
    public void preserveOriginalLineBreaks() {
        final int linelength = 70;
        final boolean breakLine = false;
        final String actualBrokenText = LineBreaker.formatDocument(unbrokenText, linelength,breakLine);
        assertEquals(actualBrokenText,unbrokenText);
    }
    @Test
    public void breakShortLineTest() {
        final int linelength = 70;
        final boolean breakLine = true;
        final String unbrokenText = "Hallo\n" +
                "Wölt!\n" +
                "New paragraph\n";
        final String targetBrokenText = unbrokenText;  //since each line is < 70 chars
        final String actualBrokenText = LineBreaker.formatDocument(unbrokenText, linelength, breakLine);
        assertEquals(actualBrokenText,targetBrokenText);
    }
}
