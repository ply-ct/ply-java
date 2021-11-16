package org.plyct.plyex.openapi;

public interface ApiDoc {
    OpenApi load(String contents);
    String dump(OpenApi openApi, int indent);
}
