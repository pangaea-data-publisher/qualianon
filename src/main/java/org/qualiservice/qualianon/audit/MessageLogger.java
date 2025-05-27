package org.qualiservice.qualianon.audit;

public interface MessageLogger {

    void logInfo(String message);

    void logInfo(String message, Object o);

    void logError(String message, Exception e);

    void logDebug(String message);

    static String withExceptionMessage(String message, Exception e) {
        final StringBuilder sb = new StringBuilder(message);
        if (e != null) {
            sb.append(": ");
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

}
