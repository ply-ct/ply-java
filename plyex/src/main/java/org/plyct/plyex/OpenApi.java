package org.plyct.plyex;

import java.util.Map;

public class OpenApi {
    String openapi;
    Info info;
    Server[] servers;
    Tag[] tags;
    Map<String, Path> paths;
    Components components;
    Auth[] security;

    public class Path {
        Map<Method, Operation> operations;
    }

    public class Operation {
        String summary;
        String[] tags;
        String description;
        Parameter[] parameters;
        RequestBody requestBody;
        Map<String, Response> responses;
        CodeSample[] codeSamples; // x-codeSamples
    }

    public class RequestBody {
        String description;
        BodyContent content;
        boolean required;
    }

    public class Response {
        String description;
        BodyContent content;
    }

    public class BodyContent {
        JsonMedia applicationJson; // 'application/json'
    }

    public class JsonMedia {
        Schema schema;
        Object example; // object | string;
    }

    public class Parameter {
        String name;
        String schema;
        String description;
        ParamType in;
        boolean required;
        String format;
        Object example; // string | number | boolean
    }

    public class Schema {
        String ref; // $ref
        String type;
        Items items;
    }

    public class Components {
        Map<String, Object> schemas;
        SecuritySchemes securitySchemes;
    }

    public class SecuritySchemes {
        BearerAuth bearerAuth;
    }

    public class BearerAuth {
        String type = "http";
        String scheme = "bearer";
        String bearerFormat = "JWT";
    }

    public class Items {
        String ref; // $ref
    }

    public enum Method {
        get,
        post,
        put,
        patch,
        delete
    }

    public enum ParamType {
        path,
        query,
        header
    }

    public class CodeSample {
        String lang;
        String source;
    }

    public class Info {
        String title;
        String version;
        String termsOfService;
        Contact contact;
        License license;
    }

    public class Contact {
        String email;
    }

    public class License {
        String name;
        String url;
    }

    public class Server {
        String url;
    }

    public class Tag {
        String name;
        String description;
        Docs externalDocs;
    }

    public class Docs {
        String url;
        String description;
    }

    public class Auth {
        String[] bearerAuth;
    }
}

