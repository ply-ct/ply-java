package org.plyct.plyex;

import com.beust.jcommander.Parameter;
import org.plyct.plyex.util.CommaSplitter;

import java.io.File;
import java.util.List;

public class DocGenOptions {

    @Parameter(names = "--help", help = true, description = "This help message")
    private boolean help;
    public boolean isHelp() { return help; }

    @Parameter(names = "--root", description = "Root directory for source files (default is cwd)")
    private String root = System.getProperty("user.dir");
    public String getRoot() { return this.root; }
    public DocGenOptions root(String root) {
        this.root = root;
        return this;
    }

    /**
     * Comma-separated list of source file glob patterns
     */
    @Parameter(names = "--sources", required = true, splitter = CommaSplitter.class, description = "Comma-separated list of source file glob patterns")
    private List<String> sources;
    public List<String> getSources() { return this.sources; }
    public DocGenOptions sources(List<String> sources) {
        this.sources = sources;
        return this;
    }

    @Parameter(names = "--plugin", description = "REST framework plugin")
    private String plugin = "org.plyct.plyex.spring.PlyexSpring";
    public String getPlugin() { return this.plugin; }
    public DocGenOptions plugin(String plugin) {
        this.plugin = plugin;
        return this;
    }

    @Parameter(names = "--addMissingOperations", description = "Add path operations missing from OpenAPI")
    private boolean addMissingOperations;
    public boolean isAddMissingOperations() { return this.addMissingOperations; }
    public DocGenOptions addMissingOperations() {
        this.addMissingOperations = true;
        return this;
    }

    @Parameter(names = "--overwriteExistingMeta", description = "Overwrite pre-existing summaries, descriptions, samples, examples")
    private boolean overwriteExistingMeta;
    public boolean isOverwriteExistingMeta() { return this.overwriteExistingMeta; }
    public DocGenOptions overwriteExistingMeta() {
        this.overwriteExistingMeta = true;
        return this;
    }

    @Parameter(names = "--indent", description = "Indent for serialized API doc")
    private int indent = 2;
    public int getIndent() { return this.indent; }
    public DocGenOptions indent(int indent) {
        this.indent = indent;
        return this;
    }

    @Parameter(names = "--debug", description = "Show debug output")
    private boolean debug;
    public boolean isDebug() { return this.debug; }
    public DocGenOptions debug() {
        this.debug = true;
        return this;
    }

    @Parameter(description = "openapi_file", required = true)
    private String openApi;
    public String getOpenApi() { return this.openApi; }
    public DocGenOptions openApi(String openApi) {
        this.openApi = openApi;
        return this;
    }
}

