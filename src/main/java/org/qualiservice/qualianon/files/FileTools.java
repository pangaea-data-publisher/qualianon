package org.qualiservice.qualianon.files;

import java.io.File;

public class FileTools {

    public static boolean createDirectoryIfNotExists(File file) {
        if (file.exists()) return true;
        return file.mkdir();
    }

}
