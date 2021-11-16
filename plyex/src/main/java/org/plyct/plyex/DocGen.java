package org.plyct.plyex;

import com.beust.jcommander.JCommander;
import org.plyct.plyex.openapi.ApiDoc;
import org.plyct.plyex.openapi.JsonDoc;
import org.plyct.plyex.openapi.OpenApi;
import org.plyct.plyex.openapi.YamlDoc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class DocGen {

    private final DocGenOptions options;
    public DocGen(DocGenOptions options) {
        this.options = options;
    }

    public OpenApi doAugment() throws IOException {
        Path file = new File(options.getOpenApi()).toPath();
        String contents = new String(Files.readAllBytes(file));
        ApiDoc apiDoc = contents.startsWith("{") ? new JsonDoc() : new YamlDoc();
        OpenApi openApi = apiDoc.load(contents);

        Plyex plyex = new Plyex(options);
        try {
            plyex.augment(openApi);
            String output = apiDoc.dump(openApi, this.options.getIndent());
            Files.write(file, output.getBytes());
            return openApi;
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
