package org.qualiservice.qualianon.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Fixture {

    public static String fixture(String filename) throws IOException {
        return FileUtils.readFileToString(fixtureAsFile(filename), StandardCharsets.UTF_8);
    }

    public static File fixtureAsFile(String filename) {
        return new File("src/test/resources/" + filename);
    }

}
