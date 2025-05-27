package org.qualiservice.qualianon.model.text;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class LineIndexTest {

    @Test
    public void coordsAtMiddleLineTest() {
        final LineIndex lineIndex = new LineIndex("Hallo\nmeine Welt!\nJuhu");
        assertEquals(new Coords(2, 7), lineIndex.getCoordsAt(12));
    }

    @Test
    public void coordsAtLastLineTest() {
        final LineIndex lineIndex = new LineIndex("Hallo\nmeine Welt!");
        assertEquals(new Coords(2, 7), lineIndex.getCoordsAt(12));
    }

    @Test
    public void getFirstLineTest() {
        final String text = "Hallo\nmeine Welt!";
        final LineIndex lineIndex = new LineIndex(text);
        assertEquals("Hallo", lineIndex.getLine(1, text));
    }

    @Test
    public void getLastLineTest() {
        final String text = "Hallo\nmeine Welt!";
        final LineIndex lineIndex = new LineIndex(text);
        assertEquals("meine Welt!", lineIndex.getLine(2, text));
    }

}
