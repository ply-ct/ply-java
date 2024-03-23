package org.plyct.plyex.docgen;

import org.plyct.plyex.openapi.OpenApi;

public class JsonSchemaType {

    private String schemaType;
    public String getSchemaType() { return schemaType; }

    public JsonSchemaType(OpenApi.MediaType jsonPayload) {
        OpenApi.Schema schema = jsonPayload.schema;
        if (schema != null) {
            String ref = schema.ref;
            if (schema.type != null && schema.type.equals("array") && schema.items != null && schema.items.ref != null) {
                ref = schema.items.ref;
            }
            if (ref != null) {
                String schemaType = ref.substring(ref.lastIndexOf("/") + 1);
                if (schema.type != null && schema.type.equals("array")) schemaType = "[" + schemaType + "]";
                this.schemaType = schemaType;
            }
        }
    }

    public String getType() {
        return isArray() ? schemaType.substring(1, schemaType.length() - 1) : schemaType;
    }

    public String getTypeName() {
        if (this.schemaType == null) return null;
        boolean isArray = this.isArray();
        String type = isArray ? schemaType.substring(1, schemaType.length() - 1) : schemaType;
        return isArray ? this.pluralize(this.uncapitalize(type)) : this.uncapitalize(type);
    }

    public boolean isArray() {
        if (this.schemaType == null) return false;
        return schemaType.startsWith("[") && schemaType.endsWith("]");
    }

    /**
     * Crude pluralization -- override with JavaDocs
     */
    private String pluralize(String sing) {
        if (sing.endsWith("s") ||
                sing.endsWith("sh") ||
                sing.endsWith("ch") ||
                sing.endsWith("x") ||
                sing.endsWith("z")
        ) {
            return sing + "es";
        } else if (sing.endsWith("y")) {
            return sing.substring(0, sing.length() - 1) + "ies";
        } else {
            return sing + "s";
        }
    }

    private String capitalize(String lower) {
        return String.valueOf(lower.charAt(0)).toUpperCase() + lower.substring(1);
    }

    private String uncapitalize(String cap) {
        return String.valueOf(cap.charAt(0)).toLowerCase() + cap.substring(1);
    }

}
