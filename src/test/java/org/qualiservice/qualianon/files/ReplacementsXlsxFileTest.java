package org.qualiservice.qualianon.files;

import org.junit.Test;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.categories.Categories;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.Label;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class ReplacementsXlsxFileTest {

    @Test
    public void readWriteTest() throws IOException {
        final MessageLogger ml = mock(MessageLogger.class);

        final CategoryScheme categoryScheme = new CategoryScheme("Person")
                .addLabel(new LabelScheme("Name"))
                .addLabel(new LabelScheme("Gender"));

        final Categories categories = new Categories();
        categories.getCategoriesProperty().add(categoryScheme);

        final Replacement replacement = new Replacement(categoryScheme, ml)
                .addLabel(new Label("Name", "Frieda"))
                .addLabel(new Label("Gender", "w"));

        final List<Replacement> replacements = Collections.singletonList(replacement);

        final File file = new File("src/test/resources/fixtures/replacements-out.xlsx");

        ReplacementsXlsxFile.write(file, replacements);
        final List<Replacement> read = ReplacementsXlsxFile.read(file, categories, ml);

        assertEquals(replacements, read);
    }

}
