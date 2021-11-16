package org.plyct.plyex.test;

import org.plyct.plyex.Endpoint;
import org.plyct.plyex.plugin.PlyexPlugin;

import java.lang.reflect.Method;

public class TestPlugin implements PlyexPlugin {

    @Override
    public Endpoint[] getEndpoints(Method method) {
        switch (method.getName()) {
            case "createGreeting":
                return new Endpoint[]{ new Endpoint(Endpoint.Method.post, "/greetings") };
            case "getGreeting":
                return new Endpoint[]{ new Endpoint(Endpoint.Method.get, "greetings/{name}") };
            case "getGreetings":
                return new Endpoint[]{ new Endpoint(Endpoint.Method.get, "greetings") };
            case "updateGreeting":
                return new Endpoint[]{ new Endpoint(Endpoint.Method.put, "greetings/{name}") };
            case "deleteGreeting":
                return new Endpoint[]{ new Endpoint(Endpoint.Method.delete, "greetings/{name}") };
        }
        return null;
    }
}
