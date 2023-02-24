package org.plyct.plyex.docgen;

import org.plyct.plyex.PlyMethod;
import org.plyct.plyex.annotation.Ply;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlyProcessor extends AbstractProcessor {

    private final boolean debug;
    private final List<PlyMethod> plyMethods = new ArrayList<>();
    public List<PlyMethod> getPlyMethods() { return this.plyMethods; }

    PlyProcessor(boolean debug){
        this.debug = debug;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        if (debug) {
            System.out.println("Initializing PlyProcessor with source version: " + processingEnv.getSourceVersion());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (this.debug && !roundEnv.getRootElements().isEmpty()) {
            System.out.println("Scanning for PlyMethods in " + roundEnv.getRootElements() + ":");
        }
        for (TypeElement annotation : annotations) {
            for (Element elem : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (elem.getKind() == ElementKind.METHOD) {
                    String signature = elem.toString();
                    ExecutableElement execElem = (ExecutableElement) elem;
                    List<? extends VariableElement> params = execElem.getParameters();
                    try {
                        if (!params.isEmpty()) {
                            // remove runtime annotations from signature
                            List<String> argTypes = params.stream().map(p -> {
                                return processingEnv.getTypeUtils().asElement(p.asType()).toString();
                            }).collect(Collectors.toList());
                            signature = elem.getSimpleName().toString() + "(" + String.join(",", argTypes) + ")";
                        }
                    } catch (Error err) {
                        System.err.println(err.getMessage());
                        if (this.debug) {
                            err.printStackTrace();
                        }
                    }
                    PlyMethod plyMethod = new PlyMethod(
                            processingEnv.getElementUtils().getPackageOf(elem).toString(),
                            elem.getEnclosingElement().getSimpleName().toString(),
                            elem.getSimpleName().toString(),
                            signature);
                    if (this.debug) {
                        System.out.println("  - " + plyMethod);
                    }
                    Ply ply = elem.getAnnotation(Ply.class);
                    plyMethod.setRequest(ply.request());
                    plyMethod.setResponses(ply.responses());
                    plyMethod.setDocComment(processingEnv.getElementUtils().getDocComment(elem));
                    this.plyMethods.add(plyMethod);
                }
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Ply.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
