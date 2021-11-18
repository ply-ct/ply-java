package org.plyct.plyex.code;

public class Item {
    private String name;
    public String getName() { return name; }

    private Object value;
    public Object getValue() { return value; }

    public Item(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
