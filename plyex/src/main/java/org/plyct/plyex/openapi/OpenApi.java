package org.plyct.plyex.openapi;

import com.google.gson.annotations.SerializedName;

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
    public ExternalDoc[] externalDocs;

    public static class Path {
        public String summary;
        public String description;

        public Operation get;
        public Operation post;
        public Operation put;
        public Operation patch;
        public Operation delete;
        public Operation options;
        public Operation head;

        public Parameter[] parameters;

        public Map<String, Operation> operations() {
            Map<String,Operation> operations = new HashMap<>();
            if (this.get != null) operations.put("get", this.get);
            if (this.post != null) operations.put("post", this.post);
            if (this.put != null) operations.put("put", this.put);
            if (this.patch != null) operations.put("patch", this.patch);
            if (this.delete != null) operations.put("delete", this.delete);
            if (this.options != null) operations.put("options", this.options);
            if (this.head != null) operations.put("head", this.head);
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
                case "options":
                    this.options = operation;
                    break;
                case "head":
                    this.head = operation;
                    break;
            }
        }
    }

    public static class Operation {
        public String operationId;
        public String summary;
        public String description;
        public String[] tags;
        public ExternalDoc[] externalDocs;
        public Parameter[] parameters;
        public RequestBody requestBody;
        public Map<String, Response> responses;
        public Boolean deprecated;
        public SecuritySchemes security;
        public Server[] servers;
        @SerializedName("x-codeSamples")
        public CodeSample[] codeSamples; // x-codeSamples
        public CodeSample[] getCodeSamples() { return this.codeSamples; }
        public void setCodeSamples(CodeSample[] codeSamples) { this.codeSamples = codeSamples; }
    }

    public static class ExternalDoc {
        public String description;
        public String url;
    }

    public static class Response {
        public String description;
        public Map<String,Header> headers;
        public BodyContent content;
    }

    public static class Header {
        String description;
        Schema schema;
    }

    public static class BodyContent {
        @SerializedName("application/json")
        public MediaType applicationJson; // 'application/json'
        public MediaType getApplicationJson() { return applicationJson; }
        public void setApplicationJson(MediaType applicationJson) { this.applicationJson = applicationJson; }

        @SerializedName("text/plain")
        public MediaType textPlain; // 'text/plain'
        public MediaType getTextPlain() { return textPlain; }
        public void setTextPlain(MediaType textPlain) { this.textPlain = textPlain; }

        @SerializedName("text/csv")
        public MediaType textCsv; // 'textCsv'
        public MediaType getTextCsv() { return textCsv; }
        public void setTextCsv(MediaType textCsv) { this.textCsv = textCsv; }

        @SerializedName("application/xml")
        public MediaType applicationXml; // 'application/xml'
        public MediaType getApplicationXml() { return applicationXml; }
        public void setApplicationXml(MediaType applicationXml) { this.applicationXml = applicationXml; }

        @SerializedName("application/octet-stream")
        public MediaType applicationOctetStream; // 'application/octet-stream'
        public MediaType getApplicationOctetStream() { return applicationOctetStream; }
        public void setApplicationOctetStream(MediaType applicationOctetStream) { this.applicationOctetStream = applicationOctetStream; }

        @SerializedName("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        public MediaType applicationXlsx; // 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        public MediaType getApplicationXlsx() { return applicationXlsx; }
        public void setApplicationXlsx(MediaType applicationXlsx) { this.applicationXlsx = applicationXlsx; }

        @SerializedName("*/*")
        public MediaType starStar; // '*/*' Spring REST declares this if no produces
        public MediaType getStarStar() { return starStar; }
        public void setStarStar(MediaType starStar) { this.starStar = starStar; }

    }

    public static class MediaType {
        public Schema schema;
        public Object example; // object | string;
    }

    public static class Schema {
        @SerializedName("$ref")
        public String ref; // $ref
        public String getRef() { return ref; }
        public void setRef(String ref) { this.ref = ref; }

        public String type;
        public String format;
        public Items items;
        public Items[] allOf;
        public Items[] oneOf;
        public Items[] anyOf;
        public Schema additionalProperties;

        @SerializedName("enum")
        public String[] enumVals;
        public String[] getEnumVals() { return this.enumVals; }
        public void setEnumVals(String[] enumVals) { this.enumVals = enumVals; }
    }

    public static class Items {
        @SerializedName("$ref")
        public String ref; // $ref
        public String getRef() { return ref; }
        public void setRef(String ref) { this.ref = ref; }

        public String type;
    }

    public static class Info {
        public String title;
        public String description;
        public String version;
        public String termsOfService;
        public Contact contact;
        public License license;
    }

    public static class Server {
        public String url;
        public String description;
        public Map<String,Object> variables;
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
        public String name;
        public String url;
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
        public Boolean required;
        public BodyContent content;
    }

    public static class Parameter {
        public String name;
        public String description;
        public ParamType in;
        public Boolean required;
        public Boolean deprecated;
        public Boolean allowEmptyValue;
        public String style;
        public Boolean explode;
        public Boolean allowReserved;

        public Schema schema;
        public Object example; // string | number | Boolean
        public Map<String,Object> examples;
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

