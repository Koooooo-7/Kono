package com.koy.kono.kono.core;

import lombok.SneakyThrows;
import org.reflections.Reflections;

import java.util.*;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class ControllerClassLoader {

    protected Set<Class<? extends BaseController>> findControllerClasses(Configuration configuration) {
        String packageName = configuration.getControllerLocation();
        Reflections reflections = new Reflections(packageName);

        Set<Class<? extends BaseController>> classes = reflections.getSubTypesOf(BaseController.class);
        if (classes.isEmpty()) {
            throw new IllegalStateException("no controller can be found under the declare controller location");
        }
        return classes;
    }
}

