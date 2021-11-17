package org.plyct.plyex;

import java.util.Map;

public class PlyResponse {
    public static class Status {
        public int code;
        public String message;
    }

    public Status status;
    public Map<String,String> headers;
    public String body;
}
