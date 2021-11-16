package org.plyct.plyex.test;

import org.junit.jupiter.api.Test;
import org.plyct.plyex.Endpoint;
import static org.plyct.plyex.Endpoint.Method.*;
import org.plyct.plyex.spring.PlyexSpring;
import org.plyct.plyex.test.spring.ClassMethod;
import org.plyct.plyex.test.spring.MethodMappings;
import org.plyct.plyex.test.spring.MultiplePaths;
import org.plyct.plyex.test.spring.NoClassMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import static org.junit.jupiter.api.Assertions.*;

public class SpringEndpointsTest {

    private Endpoint[] getEndpoints(Class<?> clazz) {
        PlyexSpring plyexSpring = new PlyexSpring();
        List<Endpoint> endpoints = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            for (Endpoint endpoint : plyexSpring.getEndpoints(method)) {
                endpoints.add(endpoint);
            }
        }
        return endpoints.toArray(new Endpoint[0]);
    }

    private boolean contains(Endpoint endpoint, Endpoint[] endpoints) {
        return Arrays.stream(endpoints).anyMatch(ep -> Objects.equals(ep, endpoint));
    }

    @Test
    void testClassMethod() {
        Endpoint[] endpoints = this.getEndpoints(ClassMethod.class);
        assertTrue(contains(new Endpoint(get, "/something"), endpoints));
        assertEquals(1, endpoints.length);
    }

    @Test
    void testMultiplePaths() {
        Endpoint[] endpoints = this.getEndpoints(MultiplePaths.class);
        assertTrue(contains(new Endpoint(get, "/heres/something"), endpoints));
        assertTrue(contains(new Endpoint(get, "/theres/something"), endpoints));
        assertTrue(contains(new Endpoint(post, "/heres/anything"), endpoints));
        assertTrue(contains(new Endpoint(post, "/theres/anything"), endpoints));
        assertTrue(contains(new Endpoint(post, "/heres/nothing"), endpoints));
        assertTrue(contains(new Endpoint(post, "/theres/nothing"), endpoints));
        assertEquals(6, endpoints.length);
    }

    @Test
    void testMethodMappings() {
        Endpoint[] endpoints = this.getEndpoints(MethodMappings.class);
        assertTrue(contains(new Endpoint(get, "/heres"), endpoints));
        assertTrue(contains(new Endpoint(delete, "/heres"), endpoints));
        assertTrue(contains(new Endpoint(post, "/heres/something"), endpoints));
        assertTrue(contains(new Endpoint(delete, "/heres/something"), endpoints));
        assertTrue(contains(new Endpoint(put, "/heres/somethingElse"), endpoints));
        assertTrue(contains(new Endpoint(delete, "/heres/somethingElse"), endpoints));
        assertTrue(contains(new Endpoint(patch, "/heres/somethingElseAgain"), endpoints));
        assertTrue(contains(new Endpoint(delete, "/heres/somethingElseAgain"), endpoints));
        assertEquals(8, endpoints.length);
    }

    @Test
    void testNoClassMapping() {
        Endpoint[] endpoints = this.getEndpoints(NoClassMapping.class);
        assertTrue(contains(new Endpoint(get, "/something"), endpoints));
        assertTrue(contains(new Endpoint(get, "/anotherThing"), endpoints));
        assertTrue(contains(new Endpoint(get, "/yetAnotherThing"), endpoints));
        assertEquals(3, endpoints.length);
    }
}
