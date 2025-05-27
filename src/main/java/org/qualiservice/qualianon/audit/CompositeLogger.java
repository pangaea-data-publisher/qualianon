package org.qualiservice.qualianon.audit;

import java.util.LinkedList;
import java.util.List;


public class CompositeLogger implements MessageLogger {

    private final List<MessageLogger> loggers;

    public CompositeLogger() {
        loggers = new LinkedList<>();
    }

    public void addLogger(MessageLogger messageLogger) {
        loggers.add(messageLogger);
    }

    @Override
    public void logInfo(String message) {
        loggers.forEach(messageLogger -> messageLogger.logInfo(message));
    }

    @Override
    public void logInfo(String message, Object o) {
        loggers.forEach(messageLogger -> messageLogger.logInfo(message, o));
    }

    @Override
    public void logError(String message, Exception e) {
        loggers.forEach(messageLogger -> messageLogger.logError(message, e));
    }

    @Override
    public void logDebug(String message) {
        loggers.forEach(messageLogger -> messageLogger.logDebug(message));
    }
}
