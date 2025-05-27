package org.qualiservice.qualianon.model.categories;

import javafx.scene.paint.Color;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class CategorySchemeTest {

    @Test
    public void equalsTrueTest() {
        final CategoryScheme categoryScheme1 = new CategoryScheme()
                .setName("Name")
                .setColor(Color.AQUAMARINE)
                .addLabel(new LabelScheme("Label"))
                .setCategoryList(new CategoryListScheme());
        final CategoryScheme categoryScheme2 = new CategoryScheme()
                .setName("Name")
                .setColor(Color.BEIGE)
                .addLabel(new LabelScheme("Label 2"))
                .setCategoryList(new CategoryListScheme().setFile("file.xml"));

        // Categories are equal, if they have the same name
        assertEquals(categoryScheme1, categoryScheme2);
    }

    @Test
    public void equalsFalseTest() {
        final CategoryScheme categoryScheme1 = new CategoryScheme()
                .setName("Name");
        final CategoryScheme categoryScheme2 = new CategoryScheme()
                .setName("Name 2");
        assertNotEquals(categoryScheme1, categoryScheme2);
    }

}
