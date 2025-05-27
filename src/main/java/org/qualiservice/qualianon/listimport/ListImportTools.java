package org.qualiservice.qualianon.listimport;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ListImportTools {

    public static List<String> makeHeader(int maxLevel) {
        return IntStream.range(1, maxLevel + 1).boxed()
                .map(i -> "Level " + i)
                .collect(Collectors.toList());
    }

}
