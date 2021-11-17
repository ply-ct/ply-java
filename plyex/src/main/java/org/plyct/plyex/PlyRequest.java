package org.plyct.plyex;

import java.util.Map;

public class PlyRequest {
    public enum Method {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE
    }

    public String url;
    public Method method;
    public Map<String,String> headers;
    public String body;
}
