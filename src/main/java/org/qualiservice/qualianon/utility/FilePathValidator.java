package org.qualiservice.qualianon.utility;

import java.io.File;
import java.nio.file.Files;

public class FilePathValidator {
    /**
     * Validates the provided {@link File} object.
     * Checks if the file exists, is valid (not null), and has read permissions.
     *
     * @param file the {@link File} object to validate
     * @return {@code true} if the file exists, is valid, and has read permissions;
     * {@code false} otherwise.
     */
    public static boolean isValidFile(File file) {
        if (file == null) {
            return false; // Null file object
        }

        try {
            // Check if the file exists or is readable
            return file.exists() || Files.isReadable(file.toPath());
        } catch (SecurityException e) {
            // In case of security restrictions
            return false;
        }
    }
}
