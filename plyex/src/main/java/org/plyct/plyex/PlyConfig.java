package org.plyct.plyex;

import java.io.File;

public class PlyConfig {

    public File file;

    /**
     * Default is cwd
     */
    public String testsLocation = ".";
    public String requestFiles;
    public String flowFiles;
    public String caseFiles;
    public String ignore;
    public String skip;
    /**
     * Default is testsLocation + "/results/expected"
     */
    public String expectedLocation;
    /**
     * Default is testsLocation + "/results/actual"
     */
    public String actualLocation;
    public String logLocation;
    public boolean resultFollowsRelativePath;
    public String[] valuesFiles;
    public boolean verbose;
    public boolean quiet;
    public boolean bail;
    public boolean parallel;
    public int batchRows;
    public int batchDelay;
    public int maxLoops;
    public boolean responseBodySortedKeys;
    public boolean genExcludeResponseHeaders;
    public String outputFile;
    public String[] binaryMediaTypes;
    public int prettyIndent = 2;

}
