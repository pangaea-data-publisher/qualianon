package org.qualiservice.qualianon.conversion;

import org.qualiservice.qualianon.files.XlsxReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ISCO08Conversion {

    public static void main(String[] args) throws IOException {
        convert("data/ISCO08_de.xlsx", "data/Occupation-List_de.xlsx");
        convert("data/ISCO08_en.xlsx", "data/Occupation-List_en.xlsx");
    }

    public static void convert(String sourceFile, String destFile) throws IOException {
        final List<List<String>> isco08Table = XlsxReader.read(sourceFile);

        final Map<String, List<String>> codeMap = new HashMap<>();
        isco08Table.forEach(row -> {
            if (row.get(0).equals("Order")) {
                return;
            }
            codeMap.put(row.get(2), row);
        });

        final List<String[]> collect = isco08Table.stream()
                .filter(row -> row.get(1).equals("4"))
                .map(row4 -> {
                    final String[] result = new String[4];
                    result[3] = row4.get(6);
                    final List<String> row3 = codeMap.get(row4.get(3));
                    result[2] = row3.get(6);
                    final List<String> row2 = codeMap.get(row3.get(3));
                    result[1] = row2.get(6);
                    final List<String> row1 = codeMap.get(row2.get(3));
                    result[0] = row1.get(6);
                    System.out.println(result[0] + " | " + result[1] + " | " + result[2] + " | " + result[3]);
                    return result;
                })
                .collect(Collectors.toList());

        System.out.println("Collected " + collect.size());

        final String[] columns = {"Level 1", "Level 2", "Level 3", "Level 4"};
        ConversionCommon.writeTable(destFile, collect, columns);
    }

}
