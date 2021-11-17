package org.plyct.plyex;

public class PlyConfig {
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

    public boolean verbose = false;
    public int prettyIndent = 2;
}
