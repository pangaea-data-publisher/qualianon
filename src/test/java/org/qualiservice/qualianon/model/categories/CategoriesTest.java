package org.qualiservice.qualianon.model.categories;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.scene.paint.Color;
import org.junit.Test;
import org.qualiservice.qualianon.gui.components.listimport.Importer;
import org.qualiservice.qualianon.test.Fixture;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class CategoriesTest {

    @Test
    public void serializeToXmlTest() throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        final String xmlString = xmlMapper.writeValueAsString(getTestCategories());
        assertEquals(Fixture.fixture("fixtures/categories.xml"), xmlString);
    }

    @Test
    public void deserializeFromXmlTest() throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        final Categories categories = xmlMapper.readValue(Fixture.fixture("fixtures/categories.xml"), Categories.class);
        assertEquals(getTestCategories(), categories);
    }

    private Categories getTestCategories() {
        final Categories categories = new Categories();
        categories.getCategoriesProperty()
                .add(new CategoryScheme()
                        .setName("Person")
                        .setColor(Color.valueOf("0xff0000ff"))
                        .addLabel(new LabelScheme()
                                .setName("Gender")
                                .addChoice("Male")
                                .addChoice("Female")
                                .addChoice("Diverse")
                        )
                        .addLabel(new LabelScheme()
                                .setName("Role"))
                        .addLabel(new LabelScheme()
                                .setName("Details"))
                        .setCategoryList(new CategoryListScheme()
                                .setFile("Location-List.csv")
                                .setStyle(SelectionStyle.TREE)
                                .setImporter(Importer.CUSTOM_XLSX)
                        )
                );
        return categories;
    }

}
