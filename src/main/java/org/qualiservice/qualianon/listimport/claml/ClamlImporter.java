package org.qualiservice.qualianon.listimport.claml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.qualiservice.qualianon.listimport.ListImportTools;
import org.qualiservice.qualianon.model.categories.CodingList;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ClamlImporter {

    public static CodingList read(File file) throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        final Claml claml = xmlMapper.readValue(file, Claml.class);
        if (!verify(claml)) {
            throw new IOException("File is not valid ClaML format.");
        }

        final List<List<String>> data = new LinkedList<>();
        int maxLevel = 0;
        final HashMap<String, ProcessClass> map = new HashMap<>();

        for (final ClamlClass clamlClass : claml.getClasses()) {
            final String text = clamlClass.getRubrics().stream()
                    .filter(rubric -> "preferred".equals(rubric.getKind()))
                    .map(Rubric::getLabel)
                    .map(Label::getText)
                    .findAny()
                    .orElse("<none>");

            ProcessClass superClass = null;
            if (clamlClass.getSuperClass() != null && clamlClass.getSuperClass().getCode() != null) {
                superClass = map.get(clamlClass.getSuperClass().getCode());
            }

            if (clamlClass.getSubClasses() != null && clamlClass.getSubClasses().size() > 0) {
                map.put(clamlClass.getCode(), new ProcessClass(superClass, text));

            } else {
                int level = 1;
                final List<String> line = new LinkedList<>();
                line.add(text);
                ProcessClass chain = superClass;
                while (chain != null) {
                    line.add(0, chain.getText());
                    chain = chain.getSuperClass();
                    level++;
                }
                data.add(line);
                if (level > maxLevel) {
                    maxLevel = level;
                }
            }
        }
        return new CodingList(claml.getTitle().getText(), ListImportTools.makeHeader(maxLevel), data);
    }

    private static boolean verify(Claml claml) {
        if (claml.getClasses() == null) return false;
        if (claml.getTitle() == null) return false;
        return true;
    }

}
