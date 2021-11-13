package org.plyct.plyex;

import java.util.Map;

public class OpenApi {
    private String openapi;
    public String getOpenapi() { return openapi; }
    public void setOpenapi(String openapi) { this.openapi = openapi; }

    private Info info;
    public Info getInfo() { return info; }
    public void setInfo(Info info) { this.info = info; }

    private Server[] servers;
    private Tag[] tags;
    private Map<String, Path> paths;
    private Components components;
    private Auth[] security;

    public static class Path {
        Map<Method, Operation> operations;
    }

    public static class Operation {
        private String summary;
        private String[] tags;
        private String description;
        private Parameter[] parameters;
        private RequestBody requestBody;
        private Map<String, Response> responses;
        private CodeSample[] codeSamples; // x-codeSamples
    }

    public static class RequestBody {
        private String description;
        private BodyContent content;
        private boolean required;
    }

    public static class Response {
        private String description;
        private BodyContent content;
    }

    public static class BodyContent {
        private JsonMedia applicationJson; // 'application/json'
    }

    public static class JsonMedia {
        private Schema schema;
        private Object example; // object | string;
    }

    public static class Parameter {
        private String name;
        private String schema;
        private String description;
        private ParamType in;
        private boolean required;
        private String format;
        private Object example; // string | number | boolean
    }

    public static class Schema {
        private String ref; // $ref
        private String type;
        private Items items;
    }

    public static class Components {
        private Map<String, Object> schemas;
        private SecuritySchemes securitySchemes;
    }

    public static class SecuritySchemes {
        private BearerAuth bearerAuth;
    }

    public static class BearerAuth {
        private String type = "http";
        private String scheme = "bearer";
        private String bearerFormat = "JWT";
    }

    public static class Items {
        private String ref; // $ref
    }

    private enum Method {
        get,
        post,
        put,
        patch,
        delete
    }

    private enum ParamType {
        path,
        query,
        header
    }

    public static class CodeSample {
        private String lang;
        private String source;
    }

    public static class Info {
        private String title;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        private String version;
        private String termsOfService;
        private Contact contact;
        private License license;
    }

    public static class Contact {
        private String email;
    }

    public static class License {
        private String name;
        private String url;
    }

    public static class Server {
        private String url;
    }

    public static class Tag {
        private String name;
        private String description;
        private Docs externalDocs;
    }

    public static class Docs {
        private String url;
        private String description;
    }

    public static class Auth {
        private String[] bearerAuth;
    }
}

