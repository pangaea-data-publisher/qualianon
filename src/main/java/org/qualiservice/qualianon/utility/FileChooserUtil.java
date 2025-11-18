package org.qualiservice.qualianon.utility;

import org.qualiservice.qualianon.utility.YJFileDialogDefaults;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class YJFileChooser extends FileChooser {

    public YJFileChooser() {
        super();
        setInitialDirectorySafe();
    }

    private void setInitialDirectorySafe() {
        File initial = YJFileDialogDefaults.getInitialDirectory();
        if (initial != null && initial.isDirectory()) {
            try {
                setInitialDirectory(initial);
            } catch (IllegalArgumentException e) {
                // JavaFX throws if dir doesn't exist or is invalid -> ignore
            }
        }
    }

    @Override
    public File showOpenDialog(Window ownerWindow) {
        File result = super.showOpenDialog(ownerWindow);
        if (result != null) {
            YJFileDialogDefaults.storeLastDirectory(result);
        }
        return result;
    }

    @Override
    public File showSaveDialog(Window ownerWindow) {
        File result = super.showSaveDialog(ownerWindow);
        if (result != null) {
            YJFileDialogDefaults.storeLastDirectory(result);
        }
        return result;
    }
}
