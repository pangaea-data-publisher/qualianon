package org.qualiservice.qualianon.model.text;

import org.junit.Before;
import org.junit.Test;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.project.Replacement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class ReplacementCounterTest {

    private ReplacementCounter replacementCounter;
    private MessageLogger ml;

    @Before
    public void setup() {
        replacementCounter = new ReplacementCounter();
        ml = mock(MessageLogger.class);
    }

    @Test
    public void nextTest() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        assertEquals(1, replacementCounter.get(replacement));
    }

    @Test
    public void next2SameReplacementsTest() {
        final Replacement replacement = new Replacement(new CategoryScheme("Person"), ml);
        replacementCounter.get(replacement);
        assertEquals(1, replacementCounter.get(replacement));
    }

    @Test
    public void next2DifferentReplacementsTest() {
        replacementCounter.get(new Replacement(new CategoryScheme("Person"), ml));
        assertEquals(2, replacementCounter.get(new Replacement(new CategoryScheme("Person"), ml)));
    }

    @Test
    public void next2DifferentCategoriesTest() {
        replacementCounter.get(new Replacement(new CategoryScheme("Person"), ml));
        assertEquals(1, replacementCounter.get(new Replacement(new CategoryScheme("Location"), ml)));
    }

}
