package org.plyct.plyex.openapi;

import com.google.gson.annotations.SerializedName;
import org.plyct.plyex.Endpoint;

import java.util.HashMap;
import java.util.Map;

public class OpenApi {
    public String openapi;
    public Info info;
    public Server[] servers;
    public Tag[] tags;
    public Map<String, Path> paths;
    public Components components;
    public Auth[] security;

    public static class Path {
        public Operation get;
        public Operation post;
        public Operation put;
        public Operation patch;
        public Operation delete;

        public Map<String, Operation> operations() {
            Map<String,Operation> operations = new HashMap<>();
            if (this.get != null) operations.put("get", this.get);
            if (this.post != null) operations.put("post", this.get);
            if (this.put != null) operations.put("put", this.get);
            if (this.patch != null) operations.put("patch", this.get);
            if (this.delete != null) operations.put("delete", this.get);
            return operations;
        }

        public void setOperation(String method, Operation operation) {
            switch (method) {
                case "get":
                    this.get = operation;
                    break;
                case "post":
                    this.post = operation;
                    break;
                case "put":
                    this.put = operation;
                    break;
                case "patch":
                    this.patch = operation;
                    break;
                case "delete":
                    this.delete = operation;
                    break;
            }
        }
    }

    public static class Operation {
        public String summary;
        public String[] tags;
        public String description;
        public Parameter[] parameters;
        public RequestBody requestBody;
        public Map<String, Response> responses;
        public CodeSample[] codeSamples; // x-codeSamples
    }

    public static class Response {
        public String description;
        public BodyContent content;
    }

    public static class BodyContent {
        @SerializedName("application/json")
        public JsonMedia applicationJson; // 'application/json'
        public JsonMedia getApplicationJson() { return applicationJson; }
        public void setApplicationJson(JsonMedia applicationJson) { this.applicationJson = applicationJson; }
    }

    public static class JsonMedia {
        public Schema schema;
        public Object example; // object | string;
    }

    public static class Schema {
        @SerializedName("$ref")
        public String ref; // $ref
        public String getRef() { return ref; }
        public void setRef(String ref) { this.ref = ref; }
        public String type;
        public Items items;
    }

    public static class Items {
        @SerializedName("$ref")
        public String ref; // $ref
        public String getRef() { return ref; }
        public void setRef(String ref) { this.ref = ref; }
    }

    public static class Info {
        public String title;
        public String version;
        public String termsOfService;
        public Contact contact;
        public License license;
    }

    public static class Server {
        public String url;
    }

    public static class Tag {
        public String name;
        public String description;
        public Docs externalDocs;
    }

    public static class Components {
        public Map<String, Object> schemas;
        public SecuritySchemes securitySchemes;
    }

    public static class SecuritySchemes {
        public BearerAuth bearerAuth;
    }

    public static class BearerAuth {
        public String type = "http";
        public String scheme = "bearer";
        public String bearerFormat = "JWT";
    }
    
    public static class Auth {
        public String[] bearerAuth;
    }

    public static class Contact {
        public String email;
    }

    public static class License {
        public String name;
        public String url;
    }

    public static class Docs {
        public String url;
        public String description;
    }
    
    public static class RequestBody {
        public String description;
        public boolean required;
        public BodyContent content;
    }

    public static class Parameter {
        public String name;
        public String description;
        public ParamType in;
        public boolean required;
        public Schema schema;
        public String format;
        public Object example; // string | number | boolean
    }

    public enum ParamType {
        path,
        query,
        header
    }

    public static class CodeSample {
        public String lang;
        public String source;
    }

}

