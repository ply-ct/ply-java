package org.plyct.plyex;

import com.beust.jcommander.Parameter;
import org.plyct.plyex.docgen.DocGen;

public class PlyexOptions {

    public PlyexOptions() {
    }

    public PlyexOptions(boolean debug) {
        this.debug = debug;
    }

    @Parameter(names = "--help", help = true, description = "This help message")
    private boolean help;
    public boolean isHelp() { return help; }

    @Parameter(names = "--debug", description = "Show debug output")
    private boolean debug;
    public boolean isDebug() { return this.debug; }

    @Parameter(names = "--indent", description = "Indent for serialized API doc")
    private int indent = 2;
    public int getIndent() { return this.indent; }

    @Parameter(names = "--plyConfig", description = "Path to plyconfig.json or plyconfig.yaml")
    private String plyConfigPath = "plyconfig.json";
    public String getPlyConfigPath() { return plyConfigPath; }

    private PlyConfig plyConfig;
    public PlyConfig getPlyConfig() { return this.plyConfig; };
    public void setPlyConfig(PlyConfig plyConfig) { this.plyConfig = plyConfig; }

}
