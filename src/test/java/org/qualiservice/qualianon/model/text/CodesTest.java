package org.qualiservice.qualianon.model.text;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CodesTest {

    @Test
    public void getCodeTest() {
        final UUID uuid = UUID.randomUUID();
        assertEquals("[M:" + uuid + ":]", Codes.marker(uuid));
    }

}
