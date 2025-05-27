package org.qualiservice.qualianon.gui.components;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.qualiservice.qualianon.Version;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.tabs.BaseTab;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class MessageLoggerTab extends BaseTab implements MessageLogger {

    private final Label statusLine;
    private final TextArea textArea;

    public MessageLoggerTab(Label statusLine) {
        super("Log");
        this.statusLine = statusLine;
        textArea = new TextArea();
        textArea.setEditable(false);
        // Do not consume Z key presses to prevent undo on the component
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.Z) {
                event.consume();
            }
        });
        getChildren().add(textArea);
        GuiUtility.extend(this);
        GuiUtility.extend(textArea);
    }

    @Override
    public void logDebug(String message) {
        if (!Version.DEBUG_LOGGING) return;
        writeOut("DEBUG", message);
    }

    @Override
    public void logInfo(String message) {
        writeOut("INFO", message);
    }

    @Override
    public void logInfo(String message, Object o) {
        logInfo(message);
    }

    @Override
    public void logError(String message, Exception e) {
        System.out.println("ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }

        final String err = MessageLogger.withExceptionMessage(message, e);

        Platform.runLater(() -> {
            statusLine.setText(err);
            textArea.appendText(getTime() + " ERROR: " + err + "\n");
        });
    }

    private String getTime() {
        return LocalTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    private void writeOut(String level, String message) {
        System.out.println(level + ": " + message);
        Platform.runLater(() -> {
            statusLine.setText(message);
            textArea.appendText(getTime() + " " + level + ": " + message + "\n");
        });
    }

}
