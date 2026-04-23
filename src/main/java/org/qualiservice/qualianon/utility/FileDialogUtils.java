package org.qualiservice.qualianon.utility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public final class YJFileDialogDefaults {

    private static final String PREF_NODE = "qualianon";      // any unique name
    private static final String KEY_LAST_DIR = "lastWorkingDir";

    private YJFileDialogDefaults() {
        // utility class
    }

    public static File getInitialDirectory() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        String lastDir = prefs.get(KEY_LAST_DIR, null);

        // 1) Try last working dir from prefs
        if (lastDir != null) {
            File f = new File(lastDir);
            if (f.isDirectory()) {
                return f;
            }
        }

        // 2) Fallback: user's Documents
        String userHome = System.getProperty("user.home");
        Path docs = Paths.get(userHome, "Documents");
        if (Files.isDirectory(docs)) {
            return docs.toFile();
        }

        // 3) Final fallback: home dir
        return new File(userHome);
    }

    public static void storeLastDirectory(File fileOrDir) {
        if (fileOrDir == null) {
            return;
        }

        File dir = fileOrDir.isDirectory()
                ? fileOrDir
                : fileOrDir.getParentFile();

        if (dir != null && dir.isDirectory()) {
            Preferences prefs = Preferences.userRoot().node(PREF_NODE);
            prefs.put(KEY_LAST_DIR, dir.getAbsolutePath());
        }
    }
}
