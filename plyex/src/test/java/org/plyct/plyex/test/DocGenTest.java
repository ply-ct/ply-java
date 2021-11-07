package org.plyct.plyex.test;

import org.junit.jupiter.api.Test;
import org.plyct.plyex.DocGen;
import org.plyct.plyex.Plyex;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DocGenTest {

    @Test
    void plyexAugment() throws IOException {

        Plyex plyex = new Plyex(new DocGen.Options()
                .packages(Arrays.asList(new String[] {"org/plyct/plyex/test/greetings"}))
                .sources(Arrays.asList(new String[]{"src/test/java/org/plyct/plyex/test/greetings/GreetingsEndpoint.java"}))
                .overwrite((true)
        ));

        plyex.augment(null);
        assertTrue(plyex.getOptions().isOverwrite());
    }

}
