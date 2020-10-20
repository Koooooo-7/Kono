package com.koy.kono.kono.core;

import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class ControllerFactory {
    private static final ConcurrentHashMap<String, MetaController> controllers = new ConcurrentHashMap<>();

    public ControllerFactory(Map<String, MetaController> ctl) {
        controllers.putAll(ctl);
    }

    public Map<String, MetaController> getControllers() {
        return controllers;
    }

    public void handle(MetaController handler, Method method) {
        Object target = handler.getInstance();
        try {
            method.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
