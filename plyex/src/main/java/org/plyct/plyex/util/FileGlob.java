package org.plyct.plyex.util;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.*;

public class FileGlob extends SimpleFileVisitor<Path> {

    private final Path root;
    private final PathMatcher matcher;
    private List<Path> matches = new ArrayList<>();

    /**
     * Recursively find matching files
     * @param root root directory to start search
     * @param pattern file glob pattern (relative to root)
     */
    public FileGlob(Path root, String pattern) {
        this.root = root;
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    }

    public List<Path> getMatches() throws IOException {
        Files.walkFileTree(this.root, this);
        return this.matches;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        if (this.matcher.matches(root.relativize(path)))
            this.matches.add(path);
        return CONTINUE;
    }
}