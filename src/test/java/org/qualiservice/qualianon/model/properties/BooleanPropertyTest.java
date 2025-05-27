package org.qualiservice.qualianon.model.properties;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanPropertyTest {

    @Test
    public void isPersistedOnEmptyConstructionTest() {
        final BooleanProperty bp1 = new BooleanProperty();
        assertFalse(bp1.isValue());
        assertFalse(bp1.isModified());
    }

    @Test
    public void isPersistedOnFalseConstructionTest() {
        final BooleanProperty bp1 = new BooleanProperty(false);
        assertFalse(bp1.isValue());
        assertFalse(bp1.isModified());
    }

    @Test
    public void isPersistedOnTrueConstructionTest() {
        final BooleanProperty bp1 = new BooleanProperty(true);
        assertTrue(bp1.isValue());
        assertFalse(bp1.isModified());
    }

    @Test
    public void notEqualsWithSamePersistedTest() {
        final BooleanProperty bp1 = new BooleanProperty(true);
        final BooleanProperty bp2 = new BooleanProperty(true);
        bp2.setValue(false);
        assertNotEquals(bp1, bp2);
    }

    @Test
    public void equalsWithDifferentPersistedTest() {
        final BooleanProperty bp1 = new BooleanProperty(true);
        final BooleanProperty bp2 = new BooleanProperty(false);
        bp2.setValue(true);
        assertEquals(bp1, bp2);
    }

}
