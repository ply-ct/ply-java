package org.plyct.plyex.docgen;

import org.plyct.plyex.PlyMethod;
import org.plyct.plyex.annotation.Ply;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
                    PlyMethod plyMethod = new PlyMethod(
                            processingEnv.getElementUtils().getPackageOf(elem).toString(),
                            elem.getEnclosingElement().getSimpleName().toString(),
                            elem.getSimpleName().toString(),
                            elem.toString());
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
