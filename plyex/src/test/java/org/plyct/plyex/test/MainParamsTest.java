package org.plyct.plyex.test;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.Test;
import org.plyct.plyex.PlyexOptions;
import org.plyct.plyex.docgen.DocGen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainParamsTest {

    @Test
    void parseParams() {
        String[] args = new String[] {
                "--debug",
                "docgen",
                "--sources", "SourceOne.java, path/to/SourceTwo.java,windows\\path\\to\\SourceThree.java ",
                "--overwriteExistingMeta",
                "openapi/greetings.yaml"
        };

        PlyexOptions options = new PlyexOptions();
        DocGen docGen = new DocGen(options);
        JCommander.newBuilder()
                .addObject(options)
                .addCommand("docgen", docGen)
                .build()
                .parse(args);

        assertTrue(options.isDebug());
        assertEquals(3, docGen.getSources().size(), 3);
        assertEquals("SourceOne.java", docGen.getSources().get(0));
        assertEquals("path/to/SourceTwo.java", docGen.getSources().get(1));
        assertEquals("windows\\path\\to\\SourceThree.java", docGen.getSources().get(2));
        assertEquals("openapi/greetings.yaml", docGen.getOpenApi());

        assertTrue(docGen.isOverwriteExistingMeta());
    }

    @Test
    void usageTest() {
        String[] args = new String[] { "--help" };

        PlyexOptions options = new PlyexOptions();
        DocGen docGen = new DocGen(options);
        JCommander jc = JCommander.newBuilder()
                .addObject(options)
                .addCommand("docgen", docGen)
                .build();
        jc.parse(args);

        assertTrue(options.isHelp());
        jc.usage();
    }
}
