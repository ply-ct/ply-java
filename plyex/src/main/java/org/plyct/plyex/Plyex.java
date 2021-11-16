package org.plyct.plyex;

import org.plyct.plyex.openapi.OpenApi;
import org.plyct.plyex.plugin.PlyexPlugin;

import java.util.*;

public class Plyex {

    private final DocGenOptions options;

    public Plyex(DocGenOptions options) {
        this.options = options;
    }

    public OpenApi augment(OpenApi openApi) throws DocGenException {
        return this.augment(openApi, this.getPlyMethods());
    }

    public OpenApi augment(OpenApi openApi, List<PlyMethod> plyMethods) throws DocGenException {
        if (this.options.isAddMissingOperations()) {
            if (openApi.paths == null) openApi.paths = new HashMap<>();
            for (PlyMethod plyMethod : plyMethods) {
                Endpoint endpoint = plyMethod.getEndpoint();
                OpenApi.Path openApiPath = openApi.paths.get(endpoint.getPath());
                if (openApiPath == null) {
                    openApiPath = new OpenApi.Path();
                }
                if (openApiPath.operations().get(endpoint.getMethod().toString()) == null) {
                    openApiPath.setOperation(endpoint.getMethod().toString(), newOperation(plyMethod));
                }
            }
        }

        return this.doAugment(openApi, plyMethods);
    }

    public OpenApi doAugment(OpenApi openApi, List<PlyMethod> plyMethods) {

        if (openApi.paths != null) {
            for (String path : openApi.paths.keySet()) {
                Map<String, OpenApi.Operation> operations = openApi.paths.get(path).operations();
                for (String method : operations.keySet()) {
                    OpenApi.Operation operation = operations.get(method);
                    Optional<PlyMethod> methodOpt = plyMethods.stream().filter(pm -> {
                        return pm.getEndpoint().getPath().equals(path) && pm.getEndpoint().getMethod().toString().equals(method);
                    }).findFirst();
                    if (methodOpt.isPresent()) {
                        PlyMethod plyMethod = methodOpt.get();
                        MethodMeta methodMeta = getMethodMeta(plyMethod);
                        if (methodMeta != null) {
                            boolean overwrite = this.options.isOverwriteExistingMeta();
                            if (methodMeta.getSummary() != null && (operation.summary == null || overwrite)) {
                                operation.summary = methodMeta.getSummary();
                            }
                            if (methodMeta.getDescription() != null && (operation.description == null || overwrite)) {
                                operation.description = methodMeta.getDescription();
                            }
                            this.addExamples(plyMethod, operation, methodMeta);
                        }
                    }
                }
            }
        }
        return openApi;
    }

    protected OpenApi.Operation newOperation(PlyMethod plyMethod) {
        Endpoint endpoint = plyMethod.getEndpoint();
        System.out.println("Adding OpenAPI operation: " + endpoint.getPath() + "." + endpoint.getMethod());
        OpenApi.Operation operation = new OpenApi.Operation();
        operation.summary = "";
        return operation;
    }

    protected MethodMeta getMethodMeta(PlyMethod plyMethod) {
        MethodMeta methodMeta = new MethodMeta();
        if (plyMethod.getDocComment() != null) {
            String comment = plyMethod.getDocComment().trim();
            if (!comment.isEmpty()) {
                String[] lines = comment.split("/\\r?\\n/");
                int dot = lines[0].indexOf('.');
                methodMeta.setSummary(dot > 0 && dot < lines[0].length() + 1 ? lines[0].substring(dot + 1).trim() : "");
                String descrip = "";
                for (int i = 1; i < lines.length; i++) {
                    descrip += "\n" + lines[i].trim();
                }
                if (descrip.length() > 0) methodMeta.setDescription(descrip.trim());
            }
        }
        if (plyMethod.getRequest() != null) {
            // TODO request samples
        }
        if (plyMethod.getResponses() != null && plyMethod.getResponses().length > 0) {
            // TODO response samples
        }

        return methodMeta;
    }

    protected void addExamples(PlyMethod plyMethod, OpenApi.Operation operation, MethodMeta methodMeta) {
        // TODO add examples
    }

    protected List<PlyMethod> getPlyMethods() throws DocGenException {
        List<PlyMethod> plyMethods = new DocGenCompiler(this.options).process();
        try {
            PlyexPlugin plyexPlugin = this.getPlugin();
            List<PlyMethod> extraPlyMethods = new ArrayList<>();
            for (PlyMethod plyMethod : plyMethods) {
                Endpoint[] endpoints = plyexPlugin.getEndpoints(plyMethod.getMethod());
                if (endpoints == null || endpoints.length == 0) {
                    System.out.println("Endpoint not found for ply method: " + plyMethod);
                } else {
                    plyMethod.setEndpoint(endpoints[0]);
                    if (endpoints.length > 1) {
                        for (int i = 1; i < endpoints.length; i++) {
                            PlyMethod extraPlyMethod = new PlyMethod(
                                    plyMethod.getPackageName(),
                                    plyMethod.getClassName(),
                                    plyMethod.getName(),
                                    plyMethod.getSignature()
                            );
                            plyMethod.setEndpoint(endpoints[i]);
                            extraPlyMethods.add(extraPlyMethod);
                        }
                    }
                }
            }
            plyMethods.addAll(extraPlyMethods);
            return plyMethods;
        } catch (ReflectiveOperationException ex) {
            throw new DocGenException("Cannot instantiate plugin: " + this.options.getPlugin(), ex);
        }
    }

    private PlyexPlugin getPlugin() throws ReflectiveOperationException {
        Class<? extends PlyexPlugin> pluginClass = Class.forName(this.options.getPlugin()).asSubclass(PlyexPlugin.class);
        return pluginClass.getDeclaredConstructor().newInstance();
    }
}
