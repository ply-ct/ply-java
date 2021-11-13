package org.plyct.plyex.test;

import org.junit.jupiter.api.Test;
import org.plyct.plyex.DocGen;
import org.plyct.plyex.DocGenOptions;
import org.plyct.plyex.openapi.OpenApi;
import org.plyct.plyex.openapi.YamlDoc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class DocGenTest {

    private Path getBefore() {
        return new File("src/test/resources/openapi/before/greetings.yaml").toPath();
    }

    private Path getTemp() {
        File tempDir = new File("src/test/resources/openapi/temp");
        if (!tempDir.exists()) tempDir.mkdir();
        return new File(tempDir + "/" + "greetings.yaml").toPath();
    }

    @Test
    void loadSaveYaml() throws IOException {
        String yaml = new String(Files.readAllBytes(getBefore()));
        YamlDoc yamlDoc = new YamlDoc();
        OpenApi openApi = yamlDoc.load(yaml);
        String out = yamlDoc.dump(openApi, 2);
        Files.write(getTemp(), out.getBytes(StandardCharsets.UTF_8));


        System.out.println("OPEN API: " + openApi);
    }

    @Test
    void docgenAugment() throws IOException {

        DocGenOptions options = new DocGenOptions().debug()
                .sources(Arrays.asList(new String[]{"src/test/java/org/plyct/plyex/test/greetings/GreetingsEndpoint.java"}))
                .plugin("org.plyct.plyex.test.TestPlugin")
                .overwrite()
                .debug()
                .openApi("src/test/resources/openapi/greetings.yaml");

        DocGen docgen = new DocGen(options);
        docgen.doAugment();

    }



}
