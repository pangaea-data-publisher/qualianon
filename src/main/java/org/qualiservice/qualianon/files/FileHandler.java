package org.qualiservice.qualianon.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;


public class FileHandler {

    public static File withFileExtension(File file, String extension) {
        final String fileName = file.getName();
        if ((!fileName.contains(".")) || !fileName.substring(fileName.lastIndexOf(".")).equals("." + extension)) {
            file = new File(file.getAbsolutePath() + "." + extension);
        }
        return file;
    }

    public static FileTime getModificationTime(File file) throws IOException {
        final BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        return basicFileAttributes.lastModifiedTime();
    }

}
