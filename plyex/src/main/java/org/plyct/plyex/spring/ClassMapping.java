package org.plyct.plyex.spring;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

/**
 * Consolidated Spring request mapping annotations for a class.
 */
public class ClassMapping extends Mapping {

    public ClassMapping(Class<?> clazz) {
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            getMethods().addAll(Arrays.asList(requestMapping.method()));
            String[] paths = requestMapping.path();
            if (paths.length == 0) paths = requestMapping.value();
            getPaths().addAll(normalize(Arrays.asList(paths)));
            setAnnotated(true);
        }
    }
}
