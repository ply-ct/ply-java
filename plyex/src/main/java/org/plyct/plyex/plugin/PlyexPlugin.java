package org.plyct.plyex.plugin;

import org.plyct.plyex.Endpoint;

import java.lang.reflect.Method;

@FunctionalInterface
public interface PlyexPlugin {

    /**
     * Locate method endpoints
     * @param method java method
     * @return endpoints corresponding to method
     */
    Endpoint[] getEndpoints(Method method);
}
