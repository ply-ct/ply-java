package org.plyct.plyex;

import org.plyct.plyex.plugin.PlyexPlugin;

import java.util.ArrayList;
import java.util.List;

public class Plyex {

    private final DocGenOptions options;

    public DocGenOptions getOptions() {
        return options;
    }

    public Plyex(DocGenOptions options) {
        this.options = options;
    }

    public OpenApi augment(OpenApi openApi) throws DocGenException {
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
            return openApi; // TODO clone
        } catch (ReflectiveOperationException ex) {
            throw new DocGenException("Cannot instantiate plugin: " + this.options.getPlugin(), ex);
        }
    }

    private PlyexPlugin getPlugin() throws ReflectiveOperationException {
        Class<? extends PlyexPlugin> pluginClass = Class.forName(this.options.getPlugin()).asSubclass(PlyexPlugin.class);
        return pluginClass.getDeclaredConstructor().newInstance();
    }
}
