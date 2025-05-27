package org.qualiservice.qualianon.utility;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class UserPreferencesTest {
    private ArrayList<String> initialProjectPath;
    private ArrayList<String> expectedProjectPath;
    public void initMockState(){
        initialProjectPath = UserPreferences.loadProjectPathStorage();
        ArrayList<String> initialProjectPaths = new ArrayList<>(Arrays.asList(
                "/home/user/projects/mockproject1",
                "/home/user/projects/mockproject2",
                "/home/user/projects/mockproject3"
        ));
        UserPreferences.saveProjectPathStorage(initialProjectPaths);

        expectedProjectPath = new ArrayList<>(initialProjectPaths);
        expectedProjectPath.add(
                "/home/user/projects/mockproject4"
        );
        Collections.reverse(expectedProjectPath);
    }
    public  void backToInitialState(){
       UserPreferences.saveProjectPathStorage(initialProjectPath);
    }
    @Test
    public void appendToProjectPathStorageTest(){
        initMockState();
        //Run tests
        ArrayList<String> retrievedProjectPathStorage = UserPreferences.loadProjectPathStorage();
        UserPreferences.appendCurrentProjectList(
                "/home/user/projects/mockproject4"
        );
        assertEquals(UserPreferences.loadProjectPathStorage(),expectedProjectPath);
        backToInitialState();
    }
}
