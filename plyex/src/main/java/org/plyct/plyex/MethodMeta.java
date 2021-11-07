package org.plyct.plyex;

public class MethodMeta {
    public enum HttpMethod {
        get,
        post,
        put,
        patch,
        delete
    }

    public class Examples {
        private String request;
        public String getRequest() { return this.request; }

        private String[] responses;
        public String[] getResponses() { return this.responses; }
    }

    private String path;
    public String getPath() { return this.path; }

    private HttpMethod method;
    public HttpMethod getMethod() { return this.method; }

    private String summary;
    public String getSummary() { return this.summary; }

    private String description;
    public String getDescription() { return this.description; }

    private Examples examples;
    public Examples getExamples() { return this.examples; }

    public MethodMeta(String path, HttpMethod method) {
        this.path = path;
        this.method = method;
    }
}
