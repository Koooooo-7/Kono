package com.koy.kono.kono.core;

import io.netty.util.internal.StringUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description Control all the meta info of controllers as a handler.
 */

public class ControllerFactory implements IController, IFactory {
    private static final ConcurrentHashMap<String, MetaController> controllers = new ConcurrentHashMap<>();

    @Override
    public void load(DefaultClassLoader defaultClassLoader, Configuration configuration) {
        loadControllers(defaultClassLoader, configuration);
    }

    private void loadControllers(DefaultClassLoader classLoader, Configuration configuration) {
        Set<Class<? extends BaseController>> classes = classLoader.findControllerClasses();

        for (Class<?> clz : classes) {
            this.wrapperMetaController(clz, clz.getMethods(), configuration);
        }
    }

    private void wrapperMetaController(Class<?> clz, Method[] methods, Configuration configuration) {

        try {
            Object instance = clz.getDeclaredConstructor().newInstance();
            Method getBaseRouter = clz.getMethod("getBaseRoute");
            String controllerName = (String) getBaseRouter.invoke(instance);

            // if the controllerName return '/name', should remove the / first.
            String baseRouter = "/" + controllerName
                    .replaceFirst("/", "")
                    .replaceFirst("(?i)Controller", "");
            baseRouter = getBaseRouterOnConfiguration(baseRouter, configuration);

            List<MetaController.MetaMethod> controllerMethod = new ArrayList<>();
            for (Method m : methods) {
                // controller has public and void method as router
                if (isControllerRouteMethod(m)) {
                    MetaController.MetaMethod metaMethod = new MetaController.MetaMethod(m, MetaController.MetaMethod.getMethodAccess(m.getName()));
                    controllerMethod.add(metaMethod);
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

    private void doWrapperMetaController(List<MetaController.MetaMethod> controllerMethod, Object instance, String baseRouter) {
        MetaController metaController = new MetaController(instance, controllerMethod, baseRouter);
        addMetaController(baseRouter, metaController);
    }

    private boolean isControllerRouteMethod(Method m) {
        boolean is = false;
        try {
            is = BaseController.class.isAssignableFrom(m.getDeclaringClass())
                    && Modifier.isPublic(m.getModifiers())
                    && "void".equals(m.getReturnType().getName())
                    && m.getParameterCount() == 0;
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

    @Override
    public void handle(MetaController handler, Method method) {
        Object target = handler.getInstance();
        try {
            method.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
