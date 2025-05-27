package org.qualiservice.qualianon.utility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void ellipsisNewlineShortTest() {
        final String s = "Hallo\nWelt";
        assertEquals("Hallo Welt", StringUtils.ellipsis(s, 100));
    }

    @Test
    public void ellipsisNewlineLongTest() {
        final String s = "Hallo\nWelt";
        assertEquals("Hallo ...", StringUtils.ellipsis(s, 9));
    }
}
