package org.plyct.plyex.spring;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Mapping {

    private final List<RequestMethod> methods = new ArrayList<>();
    List<RequestMethod> getMethods() { return this.methods; }

    private final List<String> paths = new ArrayList<>();
    List<String> getPaths() { return this.paths; }

    private boolean annotated;
    public boolean isAnnotated() { return this.annotated; }
    public void setAnnotated(boolean annotated) { this.annotated = annotated; }

    List<String> normalize(List<String> paths) {
        return paths.stream().map(p -> {
            String path = p;
            if (!path.startsWith("/")) path = "/" + path;
            if (path.length() > 1 && path.endsWith("/")) path = path.substring(1);
            return path;
        }).collect(Collectors.toList());
    }

}
