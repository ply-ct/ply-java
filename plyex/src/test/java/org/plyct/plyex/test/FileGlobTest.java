package org.plyct.plyex.test;

import org.junit.jupiter.api.Test;
import org.plyct.plyex.util.FileGlob;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileGlobTest {

    private Path root = Paths.get(System.getProperty("user.dir"));
    private String base = "src/test/java/org/plyct/plyex";

    @Test
    void matchByExt() throws IOException {
        List<Path> matches = new FileGlob(root, base + "/test/greetings/*.java").getMatches();
        assertEquals(matches.size(), 2);
        assertTrue(this.contains(matches, "/test/greetings/Greeting.java"));
        assertTrue(this.contains(matches, "/test/greetings/GreetingsEndpoint.java"));
    }

    @Test
    void matchRecursive() throws IOException {
        List<Path> matches = new FileGlob(root, base + "/**/*.java").getMatches();
        assertTrue(this.contains(matches, "/test/DocGenTest.java"));
        assertTrue(this.contains(matches, "/test/FileGlobTest.java"));
    }

    private boolean contains(List<Path> matches, String subpath) {
        return matches.stream().anyMatch(m -> {
            return m.toAbsolutePath().equals(Paths.get(base + subpath).toAbsolutePath());
        });
    }
}
