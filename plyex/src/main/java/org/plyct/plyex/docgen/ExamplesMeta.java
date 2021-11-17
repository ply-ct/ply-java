package org.plyct.plyex.docgen;

import org.plyct.plyex.PlyConfig;
import org.plyct.plyex.PlyMethod;
import org.plyct.plyex.PlyResult;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExamplesMeta {

    static class PlyResults {
        public Map<String, PlyResult> results;
    }

    private static final Map<File, Map<String,PlyResult>> expectedResults = new HashMap<>();

    private final PlyConfig plyConfig;

    private String request;
    public String getRequest() { return this.request; }

    private Map<Integer,String> responses;
    public Map<Integer,String> getResponses() { return this.responses; }

    public ExamplesMeta(PlyConfig plyConfig, PlyMethod plyMethod) throws IOException {
        this.plyConfig = plyConfig;
        if (plyMethod.getRequest() != null && !plyMethod.getRequest().isEmpty()) {
            PlyResult expectedResult = this.getExpectedResult(plyMethod.getRequest());
        }

        if (plyMethod.getResponses() != null && plyMethod.getResponses().length > 0) {
        }
    }

    private PlyResult getExpectedResult(String requestPath) throws IOException {
        int hash = requestPath.lastIndexOf('#');
        if (hash == -1 || hash > requestPath.length() - 1) {
            throw new Error("Ply example path must include '#<requestName>': " +  requestPath);
        }
        Path testsPath = new File(this.plyConfig.testsLocation).toPath();
        File suiteFile = new File(requestPath.substring(0, hash));
        Map<String,PlyResult> expectedResults = ExamplesMeta.expectedResults.get(suiteFile);
        if (expectedResults == null) {
            String suiteBaseName = suiteFile.getName().substring(0, suiteFile.getName().lastIndexOf("."));
            Path relPath = testsPath.relativize(suiteFile.toPath()).getParent();
            String expectedBase = this.plyConfig.expectedLocation + "/" + (relPath == null ? "" : relPath)
                    + "/" + suiteBaseName;
            if (expectedBase.endsWith(".ply")) expectedBase = expectedBase.substring(0, expectedBase.length() - 4);
            File expected = new File(expectedBase + ".yml");
            if (!expected.exists()) expected = new File(expectedBase + ".yaml");
            if (!expected.exists()) {
                throw new IOException("Expected result file not found: " + expected + "/.yml");
            }
            expectedResults = parseResults(expected);
            ExamplesMeta.expectedResults.put(suiteFile, expectedResults);
        }
        String requestName = requestPath.substring(hash + 1);
        PlyResult expectedResult = expectedResults.get(requestName);
        if (expectedResult == null) {
            throw new IOException("Expected result for " + requestName + " not found");
        }
        return expectedResult;
    }

    private Map<String, PlyResult> parseResults(File resultFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(resultFile))) {
            StringBuilder builder = new StringBuilder("results:\n");
            String line;
            while ((line = br.readLine()) != null) {
                for (int i = 0; i < this.plyConfig.prettyIndent; i++) {
                    builder.append(" ");
                }
                builder.append(line).append("\n");
            }

            Yaml yaml = new Yaml(new Constructor(PlyResults.class));
            try {
                PlyResults plyResults = yaml.load(builder.toString());
                return plyResults.results;
            } catch (YAMLException ex) {
                throw new IOException("Failed to parse " + resultFile + ": " + ex.getMessage(), ex);
            }
        }
    }

}
