package org.qualiservice.qualianon.conversion;

import org.qualiservice.qualianon.files.XlsxReader;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class WZConversion {

    public static void main(String[] args) throws IOException {
        convert("data/WZ_2008-DE-2019-11-15-Gliederung_mit_Erläuterung.xlsx", "data/EconomicBranches-List_de.xlsx");
    }

    public static void convert(String sourceFile, String destFile) throws IOException {
        final List<List<String>> table = XlsxReader.read(sourceFile, 1);

        final List<String[]> collect = new LinkedList<>();
        final Iterator<List<String>> iterator = table.iterator();
        iterator.next(); // Skip header

        String[] currentLevel = new String[5];
        for (; iterator.hasNext(); ) {
            final List<String> row = iterator.next();
            final int level = Integer.parseInt(row.get(1));
            currentLevel[level - 1] = row.get(4);
            if (level == 5) {
                collect.add(currentLevel.clone());
            }
            System.out.println(row.get(0) + " " + level);
        }

        final String[] columns = {"Level 1", "Level 2", "Level 3", "Level 4", "Level 5"};
        ConversionCommon.writeTable(destFile, collect, columns);
    }

}
