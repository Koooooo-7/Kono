package com.koy.kono.kono.core;

import com.koy.kono.kono.interceptor.IInterceptor;
import org.reflections.Reflections;

import java.util.*;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class DefaultClassLoader {

    private Configuration configuration;

    public DefaultClassLoader(Configuration configuration) {
        this.configuration = configuration;
    }

    // TODO: make ClassLoader interface
    public Set<Class<? extends IInterceptor>> findInterceptorClasses() {
        String packageName = configuration.getInterceptorLocation();
        Reflections reflections = new Reflections(packageName);

        Set<Class<? extends IInterceptor>> classes = reflections.getSubTypesOf(IInterceptor.class);
        if (classes.isEmpty()) {
            throw new IllegalStateException("no interceptor can be found under the declare interceptor location");
        }
        return classes;
    }

    public Set<Class<? extends BaseController>> findControllerClasses() {
        String packageName = configuration.getControllerLocation();
        Reflections reflections = new Reflections(packageName);

        Set<Class<? extends BaseController>> classes = reflections.getSubTypesOf(BaseController.class);
        if (classes.isEmpty()) {
            throw new IllegalStateException("no controller can be found under the declare controller location");
        }
        return classes;
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = ApplicationContext.class.getClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }
            }
        } catch (Throwable ex) {
            // uh, can not find any classloader to load resources...
        }
        return cl;
    }

}

