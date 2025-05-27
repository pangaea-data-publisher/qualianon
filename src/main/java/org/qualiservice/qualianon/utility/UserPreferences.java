package org.qualiservice.qualianon.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.prefs.Preferences;

public class UserPreferences {
    private static final String PREFERENCE_NODE_NAME = "org.qualiservice.qualianon.preferences";
    private static final String PROJECT_PATH_STORAGE_KEY = "project_path_storage";
    private static final String DEFAULT_PROJECT_PATH_STORAGE = "";
    private static final String DOCUMENT_LINE_WIDTH_KEY = "doc_line_width";
    private static final int DEFAULT_DOC_LINE_WIDTH = 70;
    private static final String DOCUMENT_LINEBREAK_KEY = "doc_linebreak";
    private static final boolean DOCUMENT_LINEBREAK = true;
    private static final String EXPORT_LINE_NUMBERS_KEY = "export_line_numbers";
    private static final boolean EXPORT_LINE_NUMBERS = true;
    private static Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);

    public static void saveLinebreakSettings(boolean enableLinebreak) {
        preferences.putBoolean(DOCUMENT_LINEBREAK_KEY, enableLinebreak);
    }
    public static boolean getLoadedLinebreakSettings() {
        return preferences.getBoolean(DOCUMENT_LINEBREAK_KEY, DOCUMENT_LINEBREAK);
    }
    public static void saveExportLineNumbers(boolean exportLineNumbers) {
        preferences.putBoolean(EXPORT_LINE_NUMBERS_KEY, exportLineNumbers);
    }
    public static boolean getExportLineNumbers() {
        return preferences.getBoolean(EXPORT_LINE_NUMBERS_KEY, EXPORT_LINE_NUMBERS);
    }
    public static void saveCurrentProjectPath(String currentProjectPath) {
        appendCurrentProjectList(currentProjectPath);
    }
    /**
     * Returns an ArrayList with Projects Paths (String)
     */
    public static ArrayList<String> loadProjectPathStorage(){
       ArrayList<String> projectPathArray = getProjectPathStorage();
        Collections.reverse(projectPathArray); //the latest opened projects should be shown the first in the list
        return projectPathArray;
    }
     private static ArrayList<String> getProjectPathStorage() {
        final String projectPathStorageRaw = preferences.get(PROJECT_PATH_STORAGE_KEY, DEFAULT_PROJECT_PATH_STORAGE);
        // Check if the retrieved value is empty or equal to the default
        if (projectPathStorageRaw.equals(DEFAULT_PROJECT_PATH_STORAGE)) {
            return new ArrayList<>(); // Return an empty ArrayList
        }
        String[] pathList =  projectPathStorageRaw.split(",");
        return new ArrayList<>(Arrays.asList(pathList));
    }
     static void saveProjectPathStorage(ArrayList<String> projectPathStorage){
        String projectPathStorageString = String.join(",",projectPathStorage);
        preferences.put(PROJECT_PATH_STORAGE_KEY,projectPathStorageString);
    }
    /**
     * Appends projectPath to list of Project paths and saves it as String in User Preferences
     * @param projectPath first String
     */
    static void appendCurrentProjectList(String projectPath) {
        final ArrayList<String> projectPathStorage = getProjectPathStorage();
        if (projectPathStorage.contains(projectPath)){
           projectPathStorage.remove(projectPath);
        }
        projectPathStorage.add(projectPath);
        saveProjectPathStorage(projectPathStorage);
    }
    public static void saveDocLineWidth(int currentDocLineWidth) {
        preferences.putInt(DOCUMENT_LINE_WIDTH_KEY, currentDocLineWidth);
    }
    public static int getLoadedDocLineWidth() {
        return preferences.getInt(DOCUMENT_LINE_WIDTH_KEY, DEFAULT_DOC_LINE_WIDTH);
    }
}
