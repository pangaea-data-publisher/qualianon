package org.qualiservice.qualianon.model.project;

import org.junit.Before;
import org.junit.Test;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.utility.UpdateListener;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ReplacementCollectionTest {

    private MessageLogger ml;

    @Before
    public void setup() {
        ml = mock(MessageLogger.class);
    }

    @Test
    public void onRenameLabelSchemeCallbackTest() {
        final LabelScheme labelScheme = new LabelScheme("Gender");
        final CategoryScheme categoryScheme = new CategoryScheme("Person").addLabel(labelScheme);
        final Replacement replacement = new Replacement(categoryScheme, ml);
        replacement.addLabel(new Label("Gender", "diverse"));
        final ReplacementCollection replacementCollection = new ReplacementCollection();
        replacementCollection.add(replacement);

        final UpdateListener updateListener = mock(UpdateListener.class);
        replacementCollection.addUpdateListener(updateListener);
        labelScheme.setName("Genderrr");
        verify(updateListener).onUpdate(eq(false));
    }

}
