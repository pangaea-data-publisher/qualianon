package org.qualiservice.qualianon.utility;

import org.qualiservice.qualianon.utility.YJFileDialogDefaults;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class JDirectoryChooser extends DirectoryChooser {

    public YJDirectoryChooser() {
        super();
        setInitialDirectorySafe();
    }

    private void setInitialDirectorySafe() {
        File initial = YJFileDialogDefaults.getInitialDirectory();
        if (initial != null && initial.isDirectory()) {
            try {
                setInitialDirectory(initial);
            } catch (IllegalArgumentException e) {
                // ignore invalid dir
            }
        }
    }

    @Override
    public File showDialog(Window ownerWindow) {
        File result = super.showDialog(ownerWindow);
        if (result != null) {
            YJFileDialogDefaults.storeLastDirectory(result);
        }
        return result;
    }
}
