package org.qualiservice.qualianon.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;


public class RecursiveDirectoryRemover {

    public static void remove(File directory) throws IOException {
        // Traverse the file tree in depth-first fashion and delete each file/directory.
        Files.walk(directory.toPath())
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
