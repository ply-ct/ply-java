package org.plyct.plyex;

import java.io.File;

public class PlyConfig {

    public File file;

    /**
     * Default is cwd
     */
    public String testsLocation = ".";

    /**
     * Default is testsLocation + "/results/expected"
     */
    public String expectedLocation;

    /**
     * Default is testsLocation + "/results/actual"
     */
    public String actualLocation;

    public String[] valuesFiles;
    public String outputFile;

    public boolean verbose = false;
    public int prettyIndent = 2;
}
