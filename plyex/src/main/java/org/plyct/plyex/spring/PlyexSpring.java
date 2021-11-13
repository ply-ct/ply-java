package org.plyct.plyex.spring;

import org.plyct.plyex.Endpoint;
import org.plyct.plyex.plugin.PlyexPlugin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: wildcard paths
 */
public class PlyexSpring implements PlyexPlugin {

    /**
     * Rules as I understand them regarding Spring's (poorly documented)
     * relationship between class-level and method-level annotations:
     *   - Cannot specify both path and value unless they're identical.
     *   - Paths in class @RequestMapping are prepended to paths in method mappings
     *     (all combinations).
     *   - Class @RequestMapping, method attribute is applied against all annotated
     *     methods (@RequestMapping or specific). Path of method annotation is exposed.
     *   - If Multiple method-specific annotations (@GetMapping, @DeleteMapping) are
     *     specified, only the first one is honored (use @RequestMapping with multiple
     *     methods for this case).
     *   - Optional path params are not supported (/orders/{orderId?}), instead use
     *     separate methods.
     */
    @Override
    public Endpoint[] getEndpoints(Method method) {
        ClassMapping classMapping = new ClassMapping(method.getDeclaringClass());
        MethodMapping methodMapping = new MethodMapping(method);

        List<String> paths = new ArrayList<>();
        if (classMapping.getPaths().size() > 0) {
            for (String classRequestPath : classMapping.getPaths()) {
                if (methodMapping.getPaths().size() > 0) {
                    for (String path : methodMapping.getPaths()) {
                        paths.add(classRequestPath + path);
                    }
                } else {
                    paths.add(classRequestPath);
                }
            }
        } else {
            paths.addAll(methodMapping.getPaths());
        }

        List<Endpoint> endpoints = new ArrayList<>();
        if (methodMapping.isAnnotated()) {
            // add classMethod endpoints
            List<RequestMethod> classMethods = classMapping.getMethods();
            for (RequestMethod classMethod : classMethods) {
                endpoints.addAll(getEndpoints(classMethod, classMethods, paths));
            }
        }
        endpoints.addAll(getEndpoints(null, methodMapping.getMethods(), paths));
        return endpoints.toArray(new Endpoint[]{});
    }

    List<Endpoint> getEndpoints(RequestMethod classMethod, List<RequestMethod> requestMethods, List<String> paths) {
        List<Endpoint> endpoints = new ArrayList<>();
        for (RequestMethod methodMethod : requestMethods) {
            Endpoint.HttpMethod httpMethod = this.getHttpMethod(methodMethod);
            for (String path : paths) {
                endpoints.add(new Endpoint(httpMethod, path));
                if (classMethod != null && !requestMethods.contains(classMethod)) {
                    endpoints.add(new Endpoint(this.getHttpMethod(classMethod), path));
                }
            }
        }
        return endpoints;
    }

    private Endpoint.HttpMethod getHttpMethod(RequestMethod requestMethod) {
        return Endpoint.HttpMethod.valueOf(requestMethod.toString().toLowerCase());
    }
}
