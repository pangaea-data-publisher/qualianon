package org.qualiservice.qualianon.gui.tools;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;
import org.controlsfx.dialog.ProgressDialog;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class StandardDialogs {

    public interface ProgressDialogRunner {
        boolean run();
    }

    public static boolean userConfirmation(String text, Window owner) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm");
        alert.setHeaderText(text);
        alert.initOwner(owner);

        final ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.OK;
    }

    public static Optional<String> textInput(String text, String defaultValue, Window owner) {
        final TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle("Input, please");
        dialog.setHeaderText(text);
        dialog.initOwner(owner);

        return dialog.showAndWait();
    }

    public static void errorAlert(String text, Window owner) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Oops, Error :-/");
        alert.setHeaderText(text);
        alert.initOwner(owner);
        alert.showAndWait();
    }

    public static boolean progressDialog(ProgressDialogRunner runnable) {
        final Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return runnable.run();
            }
        };
        final AtomicBoolean result = new AtomicBoolean(false);
        task.setOnSucceeded(event -> result.set((Boolean) event.getSource().getValue()));
        task.setOnFailed(event -> result.set(false));
        final ProgressDialog progressDialog = new ProgressDialog(task);
        new Thread(task).start();
        progressDialog.showAndWait();
        return result.get();
    }

}
