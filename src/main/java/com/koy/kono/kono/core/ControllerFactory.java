package com.koy.kono.kono.core;

import io.netty.util.internal.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class ControllerFactory {
    private static final ConcurrentHashMap<String, MetaController> controllers = new ConcurrentHashMap<>();

    public void loadControllers(Configuration configuration) {
        ControllerClassLoader controllerClassLoader = new ControllerClassLoader();
        Set<Class<? extends BaseController>> classes = controllerClassLoader.findControllerClasses(configuration);

        for (Class<?> clz : classes) {
            this.wrapperMetaController(clz, clz.getMethods(), configuration);
        }
    }

    private void wrapperMetaController(Class<?> clz, Method[] methods, Configuration configuration) {

        try {
            Object instance = clz.getDeclaredConstructor().newInstance();
            Method getBaseRouter = clz.getMethod("getBaseRoute");
            String controllerName = (String) getBaseRouter.invoke(instance);

//            controllerName = StringUtil.isNullOrEmpty(controllerName) ?
//                    name.replace(configuration.getControllerLocation(), "")
//                            .replaceFirst("(?i)Controller", "") : controllerName;

            String baseRouter = "/" + controllerName.replaceFirst("(?i)Controller", "");
            baseRouter = getBaseRouterOnConfiguration(baseRouter, configuration);

            List<Method> controllerMethod = new ArrayList<>();
            for (Method m : methods) {
                // controller has public and void method as router
                if (isControllerMethod(m) && Modifier.isPublic(m.getModifiers())) {
                    controllerMethod.add(m);
                }
            }

            doWrapperMetaController(controllerMethod, instance, baseRouter);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private String getBaseRouterOnConfiguration(String baseRouter, Configuration configuration) {
        Map<String, String> routers = configuration.getRouters();
        if (routers.isEmpty()) {
            return baseRouter;
        }

        String customRouter = routers.entrySet().stream().filter(e -> {
            String defaultBaseRouter = e.getKey();
            return baseRouter.equalsIgnoreCase(defaultBaseRouter);
        }).map(Map.Entry::getValue).findFirst().orElseGet(() -> "");

        if (StringUtil.isNullOrEmpty(customRouter)) {
            return baseRouter;
        }

        return customRouter;
    }

    private void doWrapperMetaController(List<Method> controllerMethod, Object instance, String baseRouter) {
        MetaController metaController = new MetaController(instance, controllerMethod, baseRouter);
        addMetaController(baseRouter, metaController);
    }

    private boolean isControllerMethod(Method m) {
        boolean is = false;
        try {
            is = m.getDeclaringClass().getSuperclass().isAssignableFrom(BaseController.class)
                    && "void".equals(m.getReturnType().getName());
        } catch (Exception ignore) {
        }
        return is;
    }

    private void addMetaController(String name, MetaController metaController) {
        controllers.put(name, metaController);
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
