package org.plyct.plyex;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.plyct.plyex.util.CommaSplitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DocGen {

    public static class Options {

        @Parameter(names = "--help", help = true)
        private boolean help;
        public boolean isHelp() { return help; }

        @Parameter(names = "--root", description = "Root directory for source files (default is cwd)")
        private String root = System.getProperty("user.dir");
        public String getRoot() { return this.root; }
        public Options root(String root) {
            this.root = root;
            return this;
        }

        /**
         * Comma-separated list of source file glob patterns
         */
        @Parameter(names = "--sources", required = true, splitter = CommaSplitter.class, description = "Comma-separated list of source file glob patterns")
        private List<String> sources;
        public List<String> getSources() { return this.sources; }
        public Options sources(List<String> sources) {
            this.sources = sources;
            return Options.this;
        }

        /**
         * Comma-separated list of root packages to scan for @Ply annotations
         */
        @Parameter(names = "--packages", required = true, splitter = CommaSplitter.class, description = "Comma-separated list of root packages to scan for @Ply annotations")
        private List<String> packages;
        public List<String> getPackages() { return this.packages; }
        public Options packages(List<String> packages) {
            this.packages = packages;
            return Options.this;
        }

        @Parameter(names = "--overwrite", description = "Overwrite")
        private boolean overwrite;
        public boolean isOverwrite() { return this.overwrite; }
        public Options overwrite(boolean overwrite) {
            this.overwrite = overwrite;
            return Options.this;
        }

        @Parameter(names = "--debug", description = "Overwrite")
        private boolean debug;
        public boolean isDebug() { return this.debug; }
        public Options debug(boolean debug) {
            this.debug = debug;
            return Options.this;
        }
    }

    public static class ArgsException extends Exception {
        public ArgsException(String message) {
            super(message);
        }
    }

    private Options options;
    public DocGen(Options options) {
        this.options = options;
    }

    public OpenApi doAugment() throws IOException {
        Plyex plyex = new Plyex(options);
        // TODO write output
        return plyex.augment(null);
    }

    public static void main(String[] args) throws IOException, ArgsException {
        List<String> argsList = Arrays.asList(args);
        int debugIdx = argsList.indexOf("--debug");
        boolean debug = debugIdx >= 0 && (argsList.size() == debugIdx + 1 || !"false".equals(argsList.get(debugIdx)));
        try {
            Options options = new Options();
            JCommander jc = JCommander.newBuilder().addObject(options).build();
            jc.parse(args);
            if (options.isHelp()) {
                jc.usage();
            } else {
                new DocGen(options).doAugment();
            }
        } catch (ParameterException ex) {
            if (debug) ex.printStackTrace();
            throw new ArgsException(ex.getMessage());
        }
    }
}
