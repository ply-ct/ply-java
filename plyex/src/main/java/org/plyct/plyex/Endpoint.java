package org.plyct.plyex;

import org.plyct.plyex.openapi.OpenApi;

public class Endpoint {

    public enum Method {
        get,
        post,
        put,
        patch,
        delete
    }

    public Endpoint(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    public Endpoint(Method method, String path, boolean lastSegmentOptional) {
        this(method, path);
        this.lastSegmentOptional = lastSegmentOptional;
    }

    private Method method;
    public Method getMethod() { return this.method; }

    /**
     * Path parameters are indicated by curly braces (eg: /greetings/{name}
     */
    private String path;
    public String getPath() { return this.path; }

    private boolean lastSegmentOptional;
    public boolean isLastSegmentOptional() { return this.lastSegmentOptional; }

    @Override
    public String toString() {
        return this.getMethod() + ": " + this.getPath();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Endpoint
                && other.toString().equals(this.toString())
                && ((Endpoint) other).lastSegmentOptional == this.lastSegmentOptional;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
