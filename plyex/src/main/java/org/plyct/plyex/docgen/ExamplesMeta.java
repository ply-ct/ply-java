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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExamplesMeta {

    static class PlyResults {
        public Map<String, PlyResult> results;
    }

    private static final Map<File, Map<String,PlyResult>> expectedResults = new HashMap<>();
    private static final Map<File, Map<String,PlyResult>> actualResults = new HashMap<>();

    private final PlyConfig plyConfig;

    private String request;
    public String getRequest() { return this.request; }

    private Map<Integer,String> responses;
    public Map<Integer,String> getResponses() { return this.responses; }

    public ExamplesMeta(PlyConfig plyConfig, PlyMethod plyMethod, boolean isExamplesFromActual) throws IOException {
        this.plyConfig = plyConfig;
        if (plyMethod.getRequest() != null && !plyMethod.getRequest().isEmpty()) {
            PlyResult result = this.getResult(plyMethod.getRequest(), isExamplesFromActual);
            if (result != null && result.request != null) {
                this.request = result.request.body;
            }
        }

        if (plyMethod.getResponses() != null && plyMethod.getResponses().length > 0) {
            for (String response : plyMethod.getResponses()) {
                if (!response.isEmpty()) {
                    PlyResult result = this.getResult(response, isExamplesFromActual);
                    if (result != null && result.response != null
                            && result.response.status != null && result.response.status.code > 0) {
                        if (this.responses == null) this.responses = new HashMap<>();
                        this.responses.put(result.response.status.code, result.response.body);
                    }
                }
            }
        }
    }

    /**
     * Find expected or actual result
     * @param requestPath
     * @param isActual actual instead of expected
     */
    private PlyResult getResult(String requestPath, boolean isActual) throws IOException {
        int hash = requestPath.lastIndexOf('#');
        if (hash == -1 || hash > requestPath.length() - 1) {
            throw new Error("Ply example path must include '#<requestName>': " +  requestPath);
        }
        Path testsPath = new File(this.plyConfig.testsLocation).toPath();
        Path plyConfigDir = new File(".").toPath();
        if (plyConfig.file != null && plyConfig.file.getParentFile() != null) {
            plyConfigDir = plyConfig.file.getParentFile().toPath();
        }
        boolean plyConfigInCwd = Files.isSameFile(plyConfigDir, new File(".").toPath());
        if (!testsPath.isAbsolute() && !plyConfigInCwd) {
            // test path is relative to plyconfig directory
            testsPath = new File(plyConfigDir + File.separator + testsPath).toPath().normalize();
        }
        File suiteFile = new File(requestPath.substring(0, hash));
        Map<String,PlyResult> results = isActual ?
                ExamplesMeta.actualResults.get(suiteFile) : ExamplesMeta.expectedResults.get(suiteFile);
        if (results == null) {
            String suiteBaseName = suiteFile.getName().substring(0, suiteFile.getName().lastIndexOf("."));
            Path relPath = testsPath.relativize(suiteFile.toPath()).getParent();
            String resultsBase = (isActual ? this.plyConfig.actualLocation : this.plyConfig.expectedLocation)
                    + File.separator + (relPath == null ? "" : relPath) + File.separator + suiteBaseName;
            if (resultsBase.endsWith(".ply")) resultsBase = resultsBase.substring(0, resultsBase.length() - 4);
            Path resultsPath = new File(resultsBase + ".yml").toPath();
            if (!resultsPath.isAbsolute() && !plyConfigInCwd) {
                resultsPath = new File(plyConfigDir + File.separator + resultsPath).toPath().normalize();
            }
            if (!resultsPath.toFile().exists()) {
                String exp = resultsPath.toString();
                resultsPath = new File(exp.substring(0, exp.lastIndexOf(".")) + ".yaml").toPath();
            }
            if (!resultsPath.toFile().exists()) {
                throw new IOException("Expected result file not found: " + resultsPath.normalize() + " (or .yml)");
            }
            results = parseResults(resultsPath.toFile());
            if (isActual) ExamplesMeta.actualResults.put(suiteFile, results);
            else ExamplesMeta.expectedResults.put(suiteFile, results);
        }
        String requestName = requestPath.substring(hash + 1);
        PlyResult result =  results.get(requestName);
        if (result == null) {
            throw new IOException((isActual ? "Actual" : "Expected") + " result for " + requestName + " not found");
        }
        return result;
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
