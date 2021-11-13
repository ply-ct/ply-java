package org.plyct.plyex.spring;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Consolidated Spring request mapping annotations for a class.
 */
class MethodMapping extends Mapping {

    public MethodMapping(Method method) {
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

        if (getMapping != null) {
            getMethods().add(RequestMethod.GET);
            String[] paths = getMapping.path();
            if (paths.length == 0) paths = getMapping.value();
            getPaths().addAll(normalize(Arrays.asList(paths)));
            this.setAnnotated(true);
        } else if (postMapping != null) {
            getMethods().add(RequestMethod.POST);
            String[] paths = postMapping.path();
            if (paths.length == 0) paths = postMapping.value();
            getPaths().addAll(normalize(Arrays.asList(paths)));
            this.setAnnotated(true);
        } else if (putMapping != null) {
            getMethods().add(RequestMethod.PUT);
            String[] paths = putMapping.path();
            if (paths.length == 0) paths = putMapping.value();
            getPaths().addAll(normalize(Arrays.asList(paths)));
            this.setAnnotated(true);
        } else if (patchMapping != null) {
            getMethods().add(RequestMethod.PATCH);
            String[] paths = patchMapping.path();
            if (paths.length == 0) paths = patchMapping.value();
            getPaths().addAll(normalize(Arrays.asList(paths)));
            this.setAnnotated(true);
        } else if (deleteMapping != null) {
            getMethods().add(RequestMethod.DELETE);
            String[] paths = deleteMapping.path();
            if (paths.length == 0) paths = deleteMapping.value();
            getPaths().addAll(normalize(Arrays.asList(paths)));
            this.setAnnotated(true);
        } else if (requestMapping != null) {
            getMethods().addAll(Arrays.asList(requestMapping.method()));
            String[] paths = requestMapping.path();
            if (paths.length == 0) paths = requestMapping.value();
            getPaths().addAll(normalize(Arrays.asList(paths)));
            this.setAnnotated(true);
        }
    }
}
