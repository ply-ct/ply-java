package org.plyct.plyex;

import org.plyct.plyex.annotation.Ply;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Set;

public class PlyProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println("INIT INIT INIT");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("PROCESS...");
        for (TypeElement annotation : annotations) {
            System.out.println("ANOT: " + annotation);
            for (Element elem : roundEnv.getElementsAnnotatedWith(annotation)) {
                System.out.println("ELEM: " + elem);
                if (elem.getKind() == ElementKind.METHOD) {
                    System.out.println("\nPLY METH: " + elem.getSimpleName());
                    String comment = processingEnv.getElementUtils().getDocComment(elem);
                    System.out.println("COMMENT: " + comment);
                    Ply ply = elem.getAnnotation(Ply.class);
                    System.out.println("request: " + ply.request());
                    System.out.println("responses: " + String.valueOf(ply.responses()));
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
