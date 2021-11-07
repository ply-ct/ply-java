package org.plyct.plyex;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.plyct.plyex.util.FileGlob;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.google.testing.compile.Compiler.javac;

public class PseudoCompiler {

    private boolean debug;
    private List<JavaFileObject> sourceFiles = new ArrayList<>();

    public PseudoCompiler(Path root, List<String> sources, boolean debug) throws IOException {
        if (debug) {
            System.out.println("Pseudo-compiling sources from root: " + root);
        }
        for (String source : sources) {
            for (Path match : new FileGlob(root, source).getMatches()) {
                if (debug) {
                    System.out.println("  - " + match);
                }
                this.sourceFiles.add(JavaFileObjects.forResource(match.toUri().toURL()));
            }
        }
    }

    public void doCompile() {

        com.google.testing.compile.Compiler compiler = javac().withProcessors(new PlyProcessor());

//        if (options != null) {
//            compiler = compiler.withOptions(options);
//        }

        Compilation compilation = compiler.compile(this.sourceFiles);


//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        if (compiler == null)
//            throw new CompilerException("No Java compiler available");
//
//        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
//
//        JavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
//        // MdwJavaFileManager<JavaFileManager> mdwFileManager = new MdwJavaFileManager<>(standardFileManager);
//
//        // compiler classpath
//        String pathSep = System.getProperty("path.separator");
//        String classpath = getJavaCompilerClasspath(currentPackage);
//        classpath += pathSep + getTempDir();  // include java source artifacts
//
//        String debug = "Compiling Dynamic Java classes: " + classNames;
//        if (logger.isMdwDebugEnabled()) {
//            String extra = "parent ClassLoader=" + parentLoader;
//            if (currentPackage != null)
//                extra += ", workflow package: " + currentPackage.getLabel();
//            logger.debug(debug + " (" + extra + ")");
//        }
//        else if (logger.isDebugEnabled()) {
//            logger.info(debug);
//        }
//
//        if (logger.isMdwDebugEnabled()) {
//            logger.mdwDebug("Dynamic Java Compiler Classpath: " + classpath);
//        }
//
//        // compiler options
//        List<String> options = new ArrayList<>(Arrays.asList("-g", "-classpath", classpath));
//        String extraOptions = PropertyManager.getProperty(PropertyNames.MDW_JAVA_COMPILER_OPTIONS);
//        if (extraOptions != null)
//            options.addAll(Arrays.asList(extraOptions.split(" ")));
//
//        JavaCompiler.CompilationTask compileTask = compiler.getTask(null, mdwFileManager, diagnostics, options, null, jfos);
//        boolean hasErrors = false;
//        List<String> erroredClasses = new ArrayList<>();
//        List<String> compilableClasses;
//        if (!compileTask.call()) {
//            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
//                String msg = "\nJava Compilation " + diagnostic.getKind() + ":" + diagnostic.getSource()
//                        + "(" + diagnostic.getLineNumber() + "," + diagnostic.getColumnNumber() + ")\n"
//                        + "   " + diagnostic.getMessage(null) + "\n";
//                logger.error(msg);
//                if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)) {
//                    hasErrors = true;
//                    if (diagnostic.getSource() instanceof StringJavaFileObject) {
//                        StringJavaFileObject source = (StringJavaFileObject)diagnostic.getSource();
//                        if (!erroredClasses.contains(source.getClassName()))
//                            erroredClasses.add(source.getClassName());
//                    }
//                }
//            }
//            if (hasErrors) {
//                logger.debug("Dynamic Java Compiler Classpath: " + classpath);
//                logger.error("Compilation errors in Dynamic Java. See compiler output in log for details.");
//                compilableClasses = new ArrayList<>();
//                for (String className : javaSources.keySet()) {
//                    if (!erroredClasses.contains(className))
//                        compilableClasses.add(className);
//                }
//                // recompile only compilable classes (TODO: a better way)
//                jfos.clear();
//                for (String className : compilableClasses)
//                    jfos.add(new StringJavaFileObject(className, javaSources.get(className)));
//                if (compilableClasses.size() > 0) {
//                    compileTask = compiler.getTask(null, mdwFileManager, diagnostics, options, null, jfos);
//                    compileTask.call();
//                }
//            }
//        }

    }
}
