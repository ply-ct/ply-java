package org.plyct.plyex;

import org.plyct.plyex.annotation.Ply;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.MethodsAnnotated;

class Scanner {
    private Reflections reflections;

    public Scanner(String prefix) {
        this.reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(prefix)
                .filterInputsBy(new FilterBuilder().includePackage(prefix))
                .setScanners(MethodsAnnotated));
    }

    List<PlyMethod> getPlyMethods() {
        return reflections.get(MethodsAnnotated.with(Ply.class).as(Method.class))
                .stream()
                .map(method -> new PlyMethod(method))
                .collect(Collectors.toList());
    }
}
