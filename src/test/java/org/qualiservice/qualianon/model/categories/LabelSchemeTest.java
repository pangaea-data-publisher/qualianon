package org.qualiservice.qualianon.model.categories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.qualiservice.qualianon.model.properties.StringProperty;
import org.qualiservice.qualianon.utility.UpdateListener;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class LabelSchemeTest {

    @Mock
    private UpdateListener updateListener;
    private LabelScheme labelScheme;

    @Before
    public void setup() {
        labelScheme = new LabelScheme();
    }

    @Test
    public void setNameTest() {
        labelScheme.addUpdateListener(updateListener);
        labelScheme.setName("Hello");
        assertEquals("Hello", labelScheme.getName());
        verify(updateListener).onUpdate(eq(false));
    }

    @Test
    public void setChoicesTest() {
        labelScheme.addUpdateListener(updateListener);
        labelScheme.setChoices(Collections.singletonList("Hello"));
        assertEquals("Hello", labelScheme.getChoices().get(0));
        verify(updateListener).onUpdate(eq(false));
    }

    @Test
    public void addChoiceTest() {
        labelScheme.addUpdateListener(updateListener);
        labelScheme.addChoice(new StringProperty("Hello"), -1);
        verify(updateListener).onUpdate(eq(false));
    }

    @Test
    public void removeChoiceTest() {
        final StringProperty hello = new StringProperty("Hello");
        labelScheme.addChoice(hello);
        labelScheme.addUpdateListener(updateListener);
        labelScheme.removeChoice(hello);
        verify(updateListener).onUpdate(false);
    }

}
