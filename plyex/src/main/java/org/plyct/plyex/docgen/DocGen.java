package org.plyct.plyex.docgen;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.plyct.plyex.Endpoint;
import org.plyct.plyex.PlyMethod;
import org.plyct.plyex.PlyexOptions;
import org.plyct.plyex.openapi.ApiDoc;
import org.plyct.plyex.openapi.JsonDoc;
import org.plyct.plyex.openapi.OpenApi;
import org.plyct.plyex.openapi.YamlDoc;
import org.plyct.plyex.plugin.PlyexPlugin;
import org.plyct.plyex.util.CommaSplitter;
import org.plyct.plyex.util.Json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Parameters(commandNames="docgen", commandDescription="Augment OpenAPI doc summaries, descriptions, samples/snippets")
public class DocGen {

    private final PlyexOptions options;

    public DocGen(PlyexOptions options) {
        this.options = options;
    }

    @Parameter(names = "--root", description = "Root directory for source files (default is cwd)")
    private String root = System.getProperty("user.dir");
    public String getRoot() { return this.root; }
    public DocGen root(String root) {
        this.root = root;
        return this;
    }

    /**
     * Comma-separated list of source file glob patterns
     */
    @Parameter(names = "--sources", required = true, splitter = CommaSplitter.class, description = "Comma-separated list of source file glob patterns")
    private List<String> sources;
    public List<String> getSources() { return this.sources; }
    public DocGen sources(List<String> sources) {
        this.sources = sources;
        return this;
    }

    @Parameter(names = "--plugin", description = "REST framework plugin")
    private String plugin = "org.plyct.plyex.spring.PlyexSpring";
    public String getPlugin() { return this.plugin; }
    public DocGen plugin(String plugin) {
        this.plugin = plugin;
        return this;
    }

    @Parameter(names = "--addMissingOperations", description = "Add path operations missing from OpenAPI")
    private boolean addMissingOperations;
    public boolean isAddMissingOperations() { return this.addMissingOperations; }
    public DocGen addMissingOperations() {
        this.addMissingOperations = true;
        return this;
    }

    @Parameter(names = "--overwriteExistingMeta", description = "Overwrite pre-existing summaries, descriptions, samples, examples")
    private boolean overwriteExistingMeta;
    public boolean isOverwriteExistingMeta() { return this.overwriteExistingMeta; }
    public DocGen overwriteExistingMeta() {
        this.overwriteExistingMeta = true;
        return this;
    }

    @Parameter(description = "openapi_file", required = true)
    private String openApi;
    public String getOpenApi() { return this.openApi; }
    public DocGen openApi(String openApi) {
        this.openApi = openApi;
        return this;
    }
    
    public void run() throws IOException {
        Path file = new File(getOpenApi()).toPath();
        String contents = new String(Files.readAllBytes(file));
        ApiDoc apiDoc = Json.isJson(file, contents) ? new JsonDoc() : new YamlDoc();
        OpenApi openApi = apiDoc.load(contents);

        try {
            augment(openApi);
            String output = apiDoc.dump(openApi, this.options.getIndent());
            Files.write(file, output.getBytes());
        } catch (DocGenException ex) {
            if (this.options.isDebug()) ex.printStackTrace();
            System.err.println("DocGen error: " + ex);
        }
    }

    public OpenApi augment(OpenApi openApi) throws DocGenException {
        return this.augment(openApi, this.getPlyMethods());
    }

    public OpenApi augment(OpenApi openApi, List<PlyMethod> plyMethods) throws DocGenException {
        if (isAddMissingOperations()) {
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
                        Endpoint endpoint = pm.getEndpoint();
                        return endpoint.getPath().equals(path) && endpoint.getMethod().toString().equals(method);
                    }).findFirst();
                    if (methodOpt.isPresent()) {
                        PlyMethod plyMethod = methodOpt.get();
                        MethodMeta methodMeta = new MethodMeta(plyMethod);
                        boolean overwrite = isOverwriteExistingMeta();
                        if (methodMeta.getSummary() != null && (operation.summary == null || overwrite)) {
                            operation.summary = methodMeta.getSummary();
                        }
                        if (methodMeta.getDescription() != null && (operation.description == null || overwrite)) {
                            operation.description = methodMeta.getDescription();
                        }
                        this.addExamples(plyMethod, operation);
                    }
                }
            }
        }
        return openApi;
    }

    protected OpenApi.Operation newOperation(PlyMethod plyMethod) {
        Endpoint endpoint = plyMethod.getEndpoint();
        System.out.println("Adding OpenAPI operation: " + endpoint.getPath() + ": " + endpoint.getMethod());
        OpenApi.Operation operation = new OpenApi.Operation();
        operation.summary = "";
        return operation;
    }

    protected void addExamples(PlyMethod plyMethod, OpenApi.Operation operation) {
        try {
            ExamplesMeta examplesMeta = new ExamplesMeta(this.options.getPlyConfig(), plyMethod);
            System.out.println(examplesMeta);

        } catch (IOException ex) {
            if (this.options.isDebug()) ex.printStackTrace();
            System.err.println("Unable to add examples for " + plyMethod.getEndpoint() + ": " + ex);
        }
    }

    protected List<PlyMethod> getPlyMethods() throws DocGenException {
        List<PlyMethod> plyMethods = new DocGenCompiler(Paths.get(getRoot()), getSources(), this.options.isDebug()).process();
        try {
            PlyexPlugin plyexPlugin = getPluginInstance();
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
            throw new DocGenException("Cannot instantiate plugin: " + getPlugin(), ex);
        }
    }

    private PlyexPlugin getPluginInstance() throws ReflectiveOperationException {
        Class<? extends PlyexPlugin> pluginClass = Class.forName(getPlugin()).asSubclass(PlyexPlugin.class);
        return pluginClass.getDeclaredConstructor().newInstance();
    }
}
