package org.qualiservice.qualianon.listimport.sgtyp;

import org.qualiservice.qualianon.files.XlsxReader;
import org.qualiservice.qualianon.model.categories.CodingList;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class SgtypImporter {

    public static CodingList read(File file) throws IOException {
        final List<List<String>> data = XlsxReader
                .read(file, 1)
                .stream()
                .skip(3)
                .filter(line -> line.size() > 9)
                .map(line -> Arrays.asList(line.get(0), line.get(7), line.get(9), line.get(3)))
                .collect(Collectors.toList());
        final List<String> header = Arrays.asList("Bundesland", "Typ einfach", "Typ differenziert", "Gemeinde");
        return new CodingList("Referenzdatei: Stadt- und Gemeindetyp, BBSR Bonn 2017", header, data);
    }

}
