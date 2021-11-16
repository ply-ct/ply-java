package org.plyct.plyex;

import org.plyct.plyex.annotation.Ply;

import java.lang.reflect.Method;

public class PlyMethod {

    /**
     * Package name for containing class
     */
    private String packageName;
    public String getPackageName() { return this.packageName; }

    /**
     * Containing class simple name
     */
    private String className;
    public String getClassName() { return this.className; }

    /**
     * Method simple name
     */
    private String name;
    public String getName() { return this.name; }

    /**
     * Method signature string with arg types
     */
    private String signature;
    public String getSignature() { return this.signature; }

    private String request;
    public String getRequest() { return this.request; }
    public void setRequest(String request) { this.request = request; }

    private String[] responses;
    public String[] getResponses() { return this.responses; }
    public void setResponses(String[] responses) { this.responses = responses; }

    private String docComment;
    public String getDocComment() { return this.docComment; }
    public void setDocComment(String docComment) { this.docComment = docComment; }

    private Method method;
    public Method getMethod() { return this.method; }

    private Endpoint endpoint;
    public Endpoint getEndpoint() { return endpoint; }
    public void setEndpoint(Endpoint endpoint) { this.endpoint = endpoint; }

    public PlyMethod(String packageName, String className, String name, String signature) throws DocGenException {
        this.packageName = packageName;
        this.className = className;
        this.name = name;
        this.signature = signature;
        try {
            this.method = findMethod();
        } catch (ReflectiveOperationException ex) {
            throw new DocGenException("Cannot reflect method: " + this, ex);
        }
        if (this.method == null) {
            throw new DocGenException("Cannot determine method: " + this);
        }
        if (this.method.getAnnotation(Ply.class) == null) {
            throw new DocGenException("Method missing @Ply annotation: " + this.method);
        }
    }

    private Method findMethod() throws ReflectiveOperationException {
        Class clazz = Class.forName(this.packageName + "." + this.className);
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(this.name)) {
                String methodString = method.toGenericString();
                int openParen = methodString.indexOf("(");
                String signature = methodString.substring(methodString.substring(0, openParen).lastIndexOf(".") + 1);
                if (signature.equals(this.signature)) {
                    return method;
                }
            }
        }
        return null;
    }

    public String toString() {
        return this.packageName + "." + this.className + "." + this.signature;
    }
}
