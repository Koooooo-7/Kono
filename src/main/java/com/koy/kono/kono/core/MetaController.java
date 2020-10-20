package com.koy.kono.kono.core;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class MetaController {

    private Object instance;
    private List<Method> methods;
    private String baseRouter;

    public MetaController(Object instance, List<Method> methods, String baseRouter) {
        this.instance = instance;
        this.methods = methods;
        this.baseRouter = baseRouter;
    }

    public Object getInstance() {
        return instance;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public String getBaseRouter() {
        return baseRouter;
    }

}
