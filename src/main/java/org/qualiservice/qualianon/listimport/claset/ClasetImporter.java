package org.qualiservice.qualianon.listimport.claset;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.qualiservice.qualianon.listimport.ListImportTools;
import org.qualiservice.qualianon.model.categories.CodingList;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


public class ClasetImporter {

    public static CodingList read(File file) throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        final Claset claset = xmlMapper.readValue(file, Claset.class);
        if (!verify(claset)) {
            throw new IOException("File is not valid Claset format.");
        }
        final String language = claset.getClassification().getLabel().getLabelText().getLanguage();

        final List<List<String>> data = new LinkedList<>();
        final Stack<String> levels = new Stack<>();
        final List<Item> itemList = claset.getClassification().getItemList();
        int maxLevel = 0;

        for (final Item item : itemList) {
            final String value = item.getValue(language);
            if (levels.size() >= item.getIdLevel()) {
                storeLine(data, levels);
                while (levels.size() >= item.getIdLevel()) {
                    levels.pop();
                }
            }
            levels.push(value);
            if (levels.size() > maxLevel) {
                maxLevel = levels.size();
            }
        }
        storeLine(data, levels);

        final String source = claset.getClassification().getLabel().getLabelText().getText();
        return new CodingList(source, ListImportTools.makeHeader(maxLevel), data);
    }

    private static boolean verify(Claset claset) {
        if (claset.getClassification() == null) return false;
        if (claset.getClassification().getLabel() == null) return false;
        if (claset.getClassification().getLabel().getLabelText() == null) return false;
        if (claset.getClassification().getItemList() == null) return false;
        return true;
    }

    private static void storeLine(List<List<String>> lines, Stack<String> levels) {
        if (levels.size() > 1) {
            lines.add((List<String>) levels.clone());
        }
    }

}
