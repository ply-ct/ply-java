package org.plyct.plyex;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import org.plyct.plyex.docgen.DocGen;
import org.plyct.plyex.util.Json;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class Plyex {

    private final PlyexOptions options;

    public Plyex(PlyexOptions options) {
        this.options = options;
    }

    public PlyConfig loadPlyConfig() throws IOException {
        File plyConfigFile = new File(options.getPlyConfigPath());
        if (!plyConfigFile.exists() && options.getPlyConfigPath().equals("plyconfig.json")) {
            // try plyconfig.yaml if not specified (because plyconfig.json is default)
            File plyConfigYaml = new File("plyconfig.yaml");
            if (!plyConfigYaml.exists()) plyConfigYaml = new File("plyconfig.yml");
            if (plyConfigYaml.exists()) plyConfigFile = plyConfigYaml;
        }
        PlyConfig plyConfig = new PlyConfig();
        if (plyConfigFile.exists()) {
            String configContents = new String(Files.readAllBytes(plyConfigFile.toPath()));
            if (Json.isJson(plyConfigFile.toPath(), configContents)) {
                plyConfig = new Gson().fromJson(configContents, PlyConfig.class);
            } else {
                plyConfig = new Yaml(new Constructor(PlyConfig.class, new LoaderOptions())).load(configContents);
            }
            if (!plyConfig.verbose) plyConfig.verbose = options.isDebug();
            if (plyConfig.prettyIndent == 2 && options.getIndent() != 2) {
                plyConfig.prettyIndent = options.getIndent();
            }
            if (plyConfig.testsLocation.endsWith("/")) {
                plyConfig.testsLocation = plyConfig.testsLocation.substring(0, plyConfig.testsLocation.length() - 1);
            }
        }
        if (plyConfig.expectedLocation == null) {
            plyConfig.expectedLocation = plyConfig.testsLocation + "/results/expected";
        }
        if (plyConfig.actualLocation == null) {
            plyConfig.actualLocation = plyConfig.testsLocation + "/results/actual";
        }
        plyConfig.file = plyConfigFile;
        return plyConfig;
    }

    public static void main(String[] args) throws ArgsException {
        List<String> argsList = Arrays.asList(args);
        int debugIdx = argsList.indexOf("--debug");
        boolean debug = debugIdx >= 0 && (argsList.size() == debugIdx + 1 || !"false".equals(argsList.get(debugIdx)));

        if (debug) {
            System.out.println("Java version: " + System.getProperty("java.version"));
        }

        try {
            PlyexOptions options = new PlyexOptions();
            DocGen docGen = new DocGen(options);
            JCommander jc = new JCommander(options);
            jc.addCommand("docgen", docGen);
            jc.parse(args);
            if (options.isHelp()) {
                jc.usage();
            } else {
                Plyex plyex = new Plyex(options);
                options.setPlyConfig(plyex.loadPlyConfig());
                docGen.run();
            }
        } catch (Exception ex) {
            if (debug) {
                ex.printStackTrace();
            }
            throw new ArgsException(ex.getMessage());
        }
    }


    public static class ArgsException extends Exception {
        public ArgsException(String message) {
            super(message);
        }
    }
}
