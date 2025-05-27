package org.qualiservice.qualianon.gui.components;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.qualiservice.qualianon.audit.MessageLogger;

public class AlertLogger implements MessageLogger {

    private final UIInterface uiInterface;
    private Alert alert;

    public AlertLogger(UIInterface uiInterface) {
        this.uiInterface = uiInterface;
    }

    @Override
    public void logInfo(String message) {
    }

    @Override
    public void logInfo(String message, Object o) {
    }

    @Override
    public void logError(String message, Exception e) {
        Platform.runLater(() -> {
            if (alert != null) {
                alert.setHeaderText(alert.getHeaderText() + ".");
                return;
            }
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Oops, Error :-/");
            alert.setHeaderText(getText(message, e));
            alert.initOwner(uiInterface.getStage());
            alert.setOnCloseRequest(event -> alert = null);
            alert.show();
        });
    }

    @Override
    public void logDebug(String message) {
    }

    private static String getText(String message, Exception e) {
        if (e == null) {
            return message;
        }
        return message + "\n" + e.getMessage();
    }
}
