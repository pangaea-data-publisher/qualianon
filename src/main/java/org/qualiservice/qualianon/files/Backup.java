package org.qualiservice.qualianon.files;

import org.qualiservice.qualianon.audit.MessageLogger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Backup {

    private final static String pattern = "yyyy_MM_dd-HH_mm_ss";
    private final File directory;
    private final MessageLogger logger;

    public Backup(File backupDirectory, MessageLogger logger) {
        this.logger = logger;
        directory = new File(
                backupDirectory.getAbsolutePath(),
                new SimpleDateFormat(pattern).format(new Date())
        );
    }

    public void add(File file) {
        if (!file.exists()) return;
        FileTools.createDirectoryIfNotExists(directory);
        final File backupFilename = new File(directory.getAbsolutePath(), file.getName());

        final boolean success = file.renameTo(backupFilename);
        if (success) {
            logger.logDebug("Created backup of file " + file.getAbsolutePath() + " to " + backupFilename.getAbsolutePath());
        } else {
            logger.logError("Cannot create backup of file " + file.getAbsolutePath() + " to " + backupFilename.getAbsolutePath(), null);
        }
    }

}
