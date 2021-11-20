package org.plyct.plyex.util;

import java.nio.file.Path;

public class Json {
    /**
     * As opposed to yaml
     * @param file json or yaml file
     * @param contents file contents
     * @return true if json
     */
    public static boolean isJson(Path file, String contents) {
        return file.getFileName().endsWith(".json") || contents.startsWith("{") || contents.startsWith("[");
    }
}
