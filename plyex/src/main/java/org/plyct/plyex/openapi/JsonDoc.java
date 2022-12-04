package org.plyct.plyex.openapi;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.StringWriter;

public class JsonDoc implements ApiDoc {

    private Gson gson;

    public JsonDoc() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
            if (src % 1 == 0) {
                return new JsonPrimitive(src.longValue());
            } else {
                return new JsonPrimitive(src);
            }
        });
        this.gson = builder.create();
    }

    public OpenApi load(String json) {
        return this.gson.fromJson(json, OpenApi.class);
    }

    public String dump(OpenApi openApi, int indent) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < indent; i++) pad.append(' ');
        jsonWriter.setIndent(pad.toString());
        this.gson.toJson(openApi, OpenApi.class, jsonWriter);
        return stringWriter.toString();
    }
}
