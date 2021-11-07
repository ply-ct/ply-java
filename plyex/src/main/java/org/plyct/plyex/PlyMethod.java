package org.plyct.plyex;

import org.plyct.plyex.annotation.Ply;

import java.lang.reflect.Method;

public class PlyMethod {
    private Method method;
    public Method getMethod() { return this.method; }

    private String request;
    public String getRequest() { return request; }

    private String[] responses;
    public String[] getResponses() { return responses; }

    private MethodMeta meta;
    public MethodMeta getMeta() { return this.meta; }

    public PlyMethod(Method method) {
        this.method = method;
        Ply ply = this.method.getAnnotation(Ply.class);
        this.request = ply.request();
        this.responses = ply.responses();
    }
}
