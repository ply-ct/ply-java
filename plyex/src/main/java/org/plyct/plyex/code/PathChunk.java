package org.plyct.plyex.code;

public class PathChunk {
    private String path;
    public String getPath() { return this.path; }
    public void setPath(String path) { this.path = path; }

    private boolean param;
    public boolean isParam() { return this.param; }
    public void setParam(boolean param) { this.param = param; }

    public PathChunk(String path) {
        this(path, false);
    }

    public PathChunk(String path, boolean param) {
        this.path = path;
        this.param = param;
    }
}
