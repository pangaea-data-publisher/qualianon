package org.qualiservice.qualianon.model.text;

import java.util.UUID;

public class Codes {

    public static String marker(UUID id) {
        return "[M:" + id + ":]";
    }

}
