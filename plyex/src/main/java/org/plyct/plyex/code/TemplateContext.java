package org.plyct.plyex.code;

public class TemplateContext {

    private PathChunk[] chunks;
    public PathChunk[] getChunks() { return this.chunks; }
    public void setChunks(PathChunk[] chunks) { this.chunks = chunks; }

    private String type;
    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }

    private String name;
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    private Item item;
    public Item getItem() { return this.item; }
    public void setItem(Item item) { this.item = item; }

    private boolean array;
    public boolean isArray() { return this.array; }
    public void setArray(boolean array) { this.array = array; }

    public TemplateContext(PathChunk[] chunks, String type, String name) {
        this.chunks = chunks;
        this.type = type;
        this.name = name;
    }
}
