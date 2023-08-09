package org.plyct.plyex;

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
        if (!this.path.startsWith("/")) this.path = "/" + this.path;
        if (this.path.length() > 1 && this.path.endsWith("/")) this.path = this.path.substring(0, this.path.length() - 1);
    }

    private Method method;
    public Method getMethod() { return this.method; }

    /**
     * Path parameters are indicated by curly braces (eg: /greetings/{name}).
     * Normalized in constructor to always start with / and with no trailing /.
     */
    private String path;
    public String getPath() { return this.path; }

    @Override
    public String toString() {
        return this.getMethod() + ": " + this.getPath();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Endpoint
                && other.toString().equals(this.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
