package org.plyct.plyex;

import com.google.testing.compile.Compiler;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.plyct.plyex.util.FileGlob;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.google.testing.compile.Compiler.javac;

public class DocGenCompiler {

    private DocGenOptions options;
    private List<JavaFileObject> sourceFiles = new ArrayList<>();

    public DocGenCompiler(DocGenOptions options) throws DocGenException {
        this.options = options;
        Path root = Paths.get(this.options.getRoot());
        List<String> sources = this.options.getSources();
        if (this.options.isDebug()) {
            System.out.println("Pseudo-compiling sources from root: " + root);
        }
        try {
            for (String source : sources) {
                for (Path match : new FileGlob(root, source).getMatches()) {
                    if (this.options.isDebug()) {
                        System.out.println("  - " + match);
                    }
                    this.sourceFiles.add(JavaFileObjects.forResource(match.toUri().toURL()));
                }
            }
        } catch (IOException ex) {
            if (this.options.isDebug()) ex.printStackTrace();
            System.err.println("Error finding sources: " + ex);
        }
    }

    public List<PlyMethod> process() {
        PlyProcessor processor = new PlyProcessor(this.options.isDebug());
        Compiler compiler = javac().withProcessors(processor);

        //  maybe accept compiler options in DocGen?
        //  if (options != null) {
        //      compiler = compiler.withOptions(options);
        //  }
        Compilation compilation = compiler.compile(this.sourceFiles);
        for (Diagnostic error : compilation.errors()) {
            System.err.println("\nJava Compilation " + error.getKind() + ":" + error.getSource()
                    + "(" + error.getLineNumber() + "," + error.getColumnNumber() + ")\n"
                    + "   " + error.getMessage(null) + "\n");
        }

        return processor.getPlyMethods();
    }
}
