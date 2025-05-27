package org.qualiservice.qualianon.utility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.qualiservice.qualianon.model.properties.StringProperty;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class ListPropertyTest {

    @Mock
    private UpdateListener updateListener;

    private ListProperty<StringProperty> listProperty;

    @Before
    public void setup() {
        listProperty = new ListProperty<>();
    }

    @Test
    public void addToListDirectUpdateTest() {
        listProperty.addUpdateListener(updateListener);
        listProperty.add(new StringProperty());
        verify(updateListener).onUpdate(eq(true));
    }

    @Test
    public void editPropIndirectUpdateTest() {
        final StringProperty stringProperty = new StringProperty();
        listProperty.add(stringProperty);
        listProperty.addUpdateListener(updateListener);

        stringProperty.setValue("Hallo Welt");
        verify(updateListener).onUpdate(eq(false));
    }

    @Test
    public void removePropTest() {
        final StringProperty stringProperty = new StringProperty();
        listProperty.add(stringProperty);
        listProperty.addUpdateListener(updateListener);

        listProperty.remove(stringProperty);
        verify(updateListener).onUpdate(eq(true));
    }

    @Test
    public void callbacksOnElementAddRemoveTest() {
        final StringProperty stringProperty = mock(StringProperty.class);
        listProperty.add(stringProperty);
        verify(stringProperty).addUpdateListener(any());
        listProperty.remove(stringProperty);
        verify(stringProperty).removeUpdateListener(any());
    }

    @Test
    public void callbacksOnElementAddClearTest() {
        final StringProperty stringProperty = mock(StringProperty.class);
        listProperty.add(stringProperty);
        verify(stringProperty).addUpdateListener(any());
        listProperty.clear();
        verify(stringProperty).removeUpdateListener(any());
    }

    @Test
    public void callbackOnClearTest() {
        listProperty.addUpdateListener(updateListener);
        listProperty.clear();
        verify(updateListener).onUpdate(eq(true));
    }
}
