package org.qualiservice.qualianon.model.project;

import org.junit.Before;
import org.junit.Test;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.utility.UpdateListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ReplacementTest {

    private MessageLogger ml;
    private LabelScheme labelScheme;
    private CategoryScheme categoryScheme;
    private Replacement replacement;
    private UpdateListener updateListener;

    @Before
    public void setup() {
        ml = mock(MessageLogger.class);
        labelScheme = new LabelScheme("Gender");
        categoryScheme = new CategoryScheme("Person");
        categoryScheme.addLabel(labelScheme);
        replacement = new Replacement(categoryScheme, ml);
        updateListener = mock(UpdateListener.class);
    }

    @Test
    public void generatesId() {
        assertNotNull(replacement.getId());
    }

    @Test
    public void renameLabelSchemeSyncsTest() {
        final Label label = new Label("Gender", "diverse");
        replacement.addLabel(label);
        labelScheme.setName("Renamed");
        assertEquals("Renamed", label.getLevel());
    }

    @Test
    public void renameLabelSchemeCallbackTest() {
        replacement.addLabel(new Label("Gender", "diverse"));
        replacement.addUpdateListener(updateListener);
        labelScheme.setName("Renamed");
        verify(updateListener).onUpdate(eq(false));
    }

    @Test
    public void changeLabelValueCallbackTest() {
        final Label label = new Label("Gender", "diverse");
        replacement.addLabel(label);
        replacement.addUpdateListener(updateListener);
        label.setValue("male");
        verify(updateListener).onUpdate(eq(false));
    }

    @Test
    public void updateLabelValueCallbackTest() {
        final Replacement replacement2 = new Replacement(categoryScheme, ml);
        replacement2.addLabel(new Label("Gender", "female"));

        replacement.addUpdateListener(updateListener);
        replacement.update(replacement2);
        verify(updateListener).onUpdate(eq(true));
    }

    @Test
    public void removePostsUpdateTest() {
        final Label label = new Label("Gender", "diverse");
        replacement.addLabel(label);
        replacement.addUpdateListener(updateListener);
        replacement.removeLabel(label);
        verify(updateListener).onUpdate(eq(true));
    }

    @Test
    public void removeTest() {
        final Label label = new Label("Gender", "diverse");
        replacement.addLabel(label);
        labelScheme.setName("Renamed");
        assertEquals("Renamed", label.getLevel());
        replacement.removeLabel(label);
        labelScheme.setName("Renamed again");
        assertEquals("Renamed", label.getLevel());
    }

    @Test
    public void updateTest() {
        final Label label = new Label("Gender", "diverse");
        replacement.addLabel(label);

        final Replacement replacement2 = new Replacement(categoryScheme, ml);
        final Label newLabel = new Label("Gender", "female");
        replacement2.addLabel(newLabel);

        replacement.update(replacement2);
        labelScheme.setName("Genderrr");

        assertEquals("Gender", label.getLevel());
        assertEquals("Genderrr", newLabel.getLevel());
    }

    @Test
    public void categoryRenameTest() {
        replacement.addUpdateListener(updateListener);
        categoryScheme.setName("Renamed");
        verify(updateListener).onUpdate(eq(false));
    }
}