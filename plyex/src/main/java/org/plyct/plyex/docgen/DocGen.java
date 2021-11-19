package org.plyct.plyex.docgen;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.plyct.plyex.Endpoint;
import org.plyct.plyex.PlyMethod;
import org.plyct.plyex.PlyexOptions;
import org.plyct.plyex.code.CodeSamples;
import org.plyct.plyex.code.Item;
import org.plyct.plyex.code.PathChunk;
import org.plyct.plyex.code.TemplateContext;
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

@Parameters(commandDescription="Augment OpenAPI doc summaries, descriptions, samples/snippets")
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
            Object requestExample = null;
            if (examplesMeta.getRequest() != null) {
                if (operation.requestBody == null) {
                    operation.requestBody = new OpenApi.RequestBody();
                    operation.requestBody.required = true;
                }
                if (operation.requestBody.content == null) {
                    operation.requestBody.content = new OpenApi.BodyContent();
                }
                if (operation.requestBody.content.applicationJson == null) {
                    operation.requestBody.content.applicationJson = new OpenApi.JsonMedia();
                }
                requestExample = this.example(plyMethod.getEndpoint(), examplesMeta.getRequest(), false);
                operation.requestBody.content.applicationJson.example = requestExample;
            }
            if (examplesMeta.getResponses() != null && !examplesMeta.getResponses().isEmpty()) {
                if (operation.responses == null) {
                    operation.responses = new HashMap<>();
                }
                for (Integer code : examplesMeta.getResponses().keySet()) {
                    OpenApi.Response openApiResponse = operation.responses.get(code.toString());
                    if (openApiResponse == null) {
                        openApiResponse = new OpenApi.Response();
                        operation.responses.put(code.toString(), openApiResponse);
                    }
                    if (openApiResponse.content == null) {
                        openApiResponse.content = new OpenApi.BodyContent();
                    }
                    if (openApiResponse.content.applicationJson == null) {
                        openApiResponse.content.applicationJson = new OpenApi.JsonMedia();
                    }
                    String response = examplesMeta.getResponses().get(code);
                    openApiResponse.content.applicationJson.example = this.example(plyMethod.getEndpoint(),
                            response, true);
                    if (openApiResponse.content.applicationJson.example != null) {
                        if (code == 200 || code == 201) {
                            this.addCodeSamples(plyMethod, operation,
                                    new JsonSchemaType(openApiResponse.content.applicationJson),
                                    requestExample,
                                    openApiResponse.content.applicationJson.example);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            if (this.options.isDebug()) ex.printStackTrace();
            System.err.println("Unable to add examples for " + plyMethod.getEndpoint() + ": " + ex);
        }
    }

    protected Object example(Endpoint endpoint, String example, boolean isResponse) {
        try {
            if (example.startsWith("{") || example.startsWith("[")) {
                GsonBuilder builder = new GsonBuilder();
                builder.setObjectToNumberStrategy(in -> {
                   double d = in.nextDouble();
                   if (d % 1 == 0) return Double.valueOf(d).longValue();
                   else return d;
                });
                Gson gson = builder.create();
                return gson.fromJson(this.cleanupJson(example), Object.class);
            }
        } catch (Exception ex) {
            if (this.options.isDebug()) ex.printStackTrace();
            System.err.println("Error parsing JSON example " + (isResponse ? "response: " : "request: ") + endpoint);
        }
        return example;
    }

    protected void addCodeSamples(PlyMethod plyMethod, OpenApi.Operation operation, JsonSchemaType schemaType,
                                  Object requestExample, Object responseExample) throws IOException {
        if (operation.codeSamples != null) return; // samples already added
        List<PathChunk> chunks = new ArrayList<>();
        chunks.add(new PathChunk(""));
        String[] pathSegments = plyMethod.getEndpoint().getPath().split("/");
        for (String segment : pathSegments) {
            PathChunk chunk = chunks.get(chunks.size() - 1);
            if (segment.startsWith("{") && segment.endsWith("}")) {
                if (chunk.isParam()) chunks.add(new PathChunk("/"));
                else chunk.setPath(chunk.getPath() + "/");
                chunks.add(new PathChunk(segment.substring(1, segment.length() - 1), true));
            } else if (chunk.isParam()) {
                chunks.add(new PathChunk("/" + segment));
            } else {
                chunk.setPath("/" + segment);
            }
        }

        TemplateContext templateContext = new TemplateContext(chunks.toArray(new PathChunk[0]), schemaType.getType(),
                schemaType.getTypeName());
        templateContext.setArray(schemaType.isArray());
        String last = pathSegments[pathSegments.length - 1];
        Object example = requestExample;
        if (plyMethod.getEndpoint().getMethod() == Endpoint.Method.get
                || plyMethod.getEndpoint().getMethod() == Endpoint.Method.delete) {
            example = responseExample;
        }
        if (last.startsWith("{") && last.endsWith("}") && example instanceof Map) {
            String name = last.substring(1, last.length() - 1);
            Object value = ((Map)example).get(name);
            if (value != null) {
                templateContext.setItem(new Item(name, value));
            }
        }
        CodeSamples codeSamples = new CodeSamples(plyMethod.getEndpoint().getMethod());
        Map<String,String> samples = codeSamples.getSamples(templateContext);
        for (String lang : samples.keySet()) {
            if (operation.codeSamples == null) operation.codeSamples = new OpenApi.CodeSample[0];
            OpenApi.CodeSample openApiCodeSample = new OpenApi.CodeSample();
            openApiCodeSample.lang = lang;
            openApiCodeSample.source = samples.get(lang);
            List<OpenApi.CodeSample> codeSampleList = new ArrayList<>(Arrays.asList(operation.codeSamples));
            codeSampleList.add(openApiCodeSample);
            operation.codeSamples = codeSampleList.toArray(new OpenApi.CodeSample[0]);
        }
    }

    protected List<PlyMethod> getPlyMethods() throws DocGenException {
        List<PlyMethod> plyMethods = new DocGenCompiler(Paths.get(getRoot()), getSources(),
                this.options.isDebug()).process();
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

    private String cleanupJson(String json) {
        List<String> lines = new ArrayList<>();
        for (String line : json.split("\r?\n")) {
            lines.add(line.replaceAll("(?<!\")\\$\\{.+?}", "123"));
        }
        return String.join(System.lineSeparator(), lines);
    }

    private PlyexPlugin getPluginInstance() throws ReflectiveOperationException {
        Class<? extends PlyexPlugin> pluginClass = Class.forName(getPlugin()).asSubclass(PlyexPlugin.class);
        return pluginClass.getDeclaredConstructor().newInstance();
    }
}
