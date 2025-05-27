package org.qualiservice.qualianon.audit;

import org.qualiservice.qualianon.Version;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FileLogger implements MessageLogger {

    private FileWriter fileWriter;
    private PrintWriter printWriter;

    @Override
    public void logInfo(String message) {
        writeOut("INFO", message);
        flush();
    }

    @Override
    public void logInfo(String message, Object o) {
        logInfo(message + "; " + o);
    }

    @Override
    public void logError(String message, Exception e) {
        if (printWriter == null) return;
        writeOut("ERROR", MessageLogger.withExceptionMessage(message, e));
        if (e != null) {
            e.printStackTrace(printWriter);
        }
        flush();
    }

    @Override
    public void logDebug(String message) {
        writeOut("DEBUG", message);
        flush();
    }

    public void openFile(File logfile) {
        closeFile();

        try {
            fileWriter = new FileWriter(logfile, true);
            printWriter = new PrintWriter(fileWriter);
            logInfo("Initializing FileLogger in " + Version.QUALI_ANON_TITLE + " " + Version.VERSION);

        } catch (IOException e) {
            closeFile();
        }
    }

    public void closeFile() {
        if (printWriter != null) {
            printWriter.close();
            printWriter = null;
        }
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException ignored) {
            }
            fileWriter = null;
        }
    }

    private void writeOut(String level, String message) {
        if (printWriter == null) return;
        printWriter.println(getTime() + " " + level + ": " + message);
    }

    private String getTime() {
        return LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private void flush() {
        if (printWriter == null) return;
        printWriter.flush();
    }
}
