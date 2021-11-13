package org.plyct.plyex.test;

import org.plyct.plyex.Endpoint;
import org.plyct.plyex.plugin.PlyexPlugin;

import java.lang.reflect.Method;

public class TestPlugin implements PlyexPlugin {

    @Override
    public Endpoint[] getEndpoints(Method method) {
        switch (method.getName()) {
            case "createGreeting":
                return new Endpoint[]{ new Endpoint(Endpoint.HttpMethod.post, "/greetings") };
            case "getGreeting":
                return new Endpoint[]{ new Endpoint(Endpoint.HttpMethod.get, "greetings/{name}") };
            case "getGreetings":
                return new Endpoint[]{ new Endpoint(Endpoint.HttpMethod.get, "greetings") };
            case "updateGreeting":
                return new Endpoint[]{ new Endpoint(Endpoint.HttpMethod.put, "greetings/{name}") };
            case "deleteGreeting":
                return new Endpoint[]{ new Endpoint(Endpoint.HttpMethod.delete, "greetings/{name}") };
        }
        return null;
    }
}
