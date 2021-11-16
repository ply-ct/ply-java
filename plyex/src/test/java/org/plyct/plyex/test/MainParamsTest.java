package org.plyct.plyex.test;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.Test;
import org.plyct.plyex.DocGenOptions;

import static org.junit.jupiter.api.Assertions.*;

public class MainParamsTest {

    @Test
    void parseParams() {
        String[] args = new String[]{
                "--sources", "SourceOne.java, path/to/SourceTwo.java,windows\\path\\to\\SourceThree.java ",
                "--overwriteExistingMeta",
                "openapi/greetings.yaml"
        };

        DocGenOptions options = new DocGenOptions();
        JCommander.newBuilder()
                .addObject(options)
                .build()
                .parse(args);

        assertEquals(3, options.getSources().size(), 3);
        assertEquals("SourceOne.java", options.getSources().get(0));
        assertEquals("path/to/SourceTwo.java", options.getSources().get(1));
        assertEquals("windows\\path\\to\\SourceThree.java", options.getSources().get(2));
        assertEquals("openapi/greetings.yaml", options.getOpenApi());

        assertTrue(options.isOverwriteExistingMeta());
    }
}
