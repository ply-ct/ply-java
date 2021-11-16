package org.plyct.plyex.test;

import org.junit.jupiter.api.Test;
import org.plyct.plyex.DocGen;
import org.plyct.plyex.DocGenOptions;
import org.plyct.plyex.openapi.JsonDoc;
import org.plyct.plyex.openapi.OpenApi;
import org.plyct.plyex.openapi.YamlDoc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DocGenTest {

    private Path getBefore(String ext) {
        return new File("src/test/resources/openapi/before/greetings." + ext).toPath();
    }

    private Path getTemp(String ext) {
        File tempDir = new File("src/test/resources/openapi/temp");
        if (!tempDir.exists()) tempDir.mkdir();
        return new File(tempDir + "/" + "greetings." + ext).toPath();
    }

    @Test
    void loadSaveYaml() throws IOException {
        String in = new String(Files.readAllBytes(getBefore("yaml")));
        YamlDoc yamlDoc = new YamlDoc();
        OpenApi openApi = yamlDoc.load(in);
        String out = yamlDoc.dump(openApi, 2);
        Files.write(getTemp("yaml"), out.getBytes(StandardCharsets.UTF_8));
        assertEquals(in, out);
    }

    @Test
    void loadSaveJson() throws IOException {
        String in = new String(Files.readAllBytes(getBefore("json")));
        JsonDoc jsonDoc = new JsonDoc();
        OpenApi openApi = jsonDoc.load(in);
        String out = jsonDoc.dump(openApi, 2);
        Files.write(getTemp("json"), out.getBytes(StandardCharsets.UTF_8));
        assertEquals(in, out);
    }

    @Test
    void docGenAugment() throws IOException {

        String outputFile = "src/test/resources/openapi/greetings.yaml";
        String in = new String(Files.readAllBytes(getBefore("yaml")));
        Files.write(new File(outputFile).toPath(), in.getBytes());

        DocGenOptions options = new DocGenOptions().debug()
                .sources(Arrays.asList(new String[]{"src/test/java/org/plyct/plyex/test/greetings/GreetingsEndpoint.java"}))
                .plugin("org.plyct.plyex.test.TestPlugin")
                .overwriteExistingMeta()
                .debug()
                .openApi(outputFile);

        DocGen docgen = new DocGen(options);
        docgen.doAugment();
    }



}
