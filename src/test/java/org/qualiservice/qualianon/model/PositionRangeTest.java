package org.qualiservice.qualianon.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PositionRangeTest {

    @Test
    public void startEndTest() {
        final PositionRange range = new PositionRange(5, 7);
        assertEquals(5, range.getStart());
        assertEquals(7, range.getEnd());
    }

}
