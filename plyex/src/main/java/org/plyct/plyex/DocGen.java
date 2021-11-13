package org.plyct.plyex;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DocGen {

    private final DocGenOptions options;
    public DocGen(DocGenOptions options) {
        this.options = options;
    }

    public OpenApi doAugment() throws IOException {
        String contents = new String(Files.readAllBytes(new File(options.getOpenApi()).toPath()));
        OpenApi openApi;
        if (contents.startsWith("{") || contents.startsWith("[")) {
            openApi = new Gson().fromJson(contents, OpenApi.class);
        } else {
            openApi = new Yaml(new Constructor(OpenApi.class)).load(contents);
        }

        Plyex plyex = new Plyex(options);
        // TODO write output
        try {
            return plyex.augment(openApi);
        } catch (DocGenException ex) {
            if (this.options.isDebug()) ex.printStackTrace();
            System.err.println("DocGen error: " + ex);
            return null;
        }
    }

    public static void main(String[] args) throws ArgsException {
        List<String> argsList = Arrays.asList(args);
        int debugIdx = argsList.indexOf("--debug");
        boolean debug = debugIdx >= 0 && (argsList.size() == debugIdx + 1 || !"false".equals(argsList.get(debugIdx)));

        try {
            DocGenOptions options = new DocGenOptions();
            JCommander jc = JCommander.newBuilder().addObject(options).build();
            jc.parse(args);
            if (options.isHelp()) {
                jc.usage();
            } else {
                new DocGen(options).doAugment();
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
