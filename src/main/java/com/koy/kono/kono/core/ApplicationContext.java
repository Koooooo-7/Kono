package com.koy.kono.kono.core;

import com.koy.kono.kono.route.DispatcherHandler;
import com.koy.kono.kono.route.RouteParser;
import io.netty.util.internal.StringUtil;
import org.reflections.Reflections;

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

public class ApplicationContext {

    private static final ThreadLocal<RequestContext> requestContext = new ThreadLocal<>();
    private static final ConcurrentHashMap<String, MetaController> metaControllers = new ConcurrentHashMap<>();
    private Configuration configuration;

    private DispatcherHandler dispatcherHandler;
    private ControllerFactory controllerFactory;

    public ApplicationContext(Configuration configuration) {
        this.configuration = configuration;
    }

    public void refresh() {
        loadControllers(configuration);
        controllerFactory = new ControllerFactory(metaControllers);
        RouteParser routeParser = new RouteParser(controllerFactory);
        dispatcherHandler = new DispatcherHandler(routeParser);
    }

    public ControllerFactory getControllerFactory() {
        return controllerFactory;
    }

    public DispatcherHandler getDispatcherHandler() {
        return dispatcherHandler;
    }

    private void loadControllers(Configuration configuration) {
        ControllerClassLoader controllerClassLoader = new ControllerClassLoader();
        Set<Class<? extends BaseController>> classes = controllerClassLoader.findControllerClasses(configuration);

        for (Class<?> clz : classes) {
            this.wrapperMetaController(clz, clz.getMethods(), configuration);
        }
    }

    private void wrapperMetaController(Class<?> clz, Method[] methods, Configuration configuration) {

        try {
            Object instance = clz.getDeclaredConstructor().newInstance();
            Method getBaseRouter = clz.getMethod("getBaseRouter");
            String controllerName = (String) getBaseRouter.invoke(instance);

//            controllerName = StringUtil.isNullOrEmpty(controllerName) ?
//                    name.replace(configuration.getControllerLocation(), "")
//                            .replaceFirst("(?i)Controller", "") : controllerName;

            String baseRouter = "/" + controllerName.replaceFirst("(?i)Controller", "");
            baseRouter = getBaseRouterOnConfiguration(baseRouter, configuration);

            List<Method> controllerMethod = new ArrayList<>();
            for (Method m : methods) {
                // controller own public method
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

        String customRouter = routers.get(baseRouter);
        if (StringUtil.isNullOrEmpty(customRouter)) {
            return customRouter;
        }
        return baseRouter;
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
        metaControllers.put(name, metaController);
    }

    private Map<String, MetaController> getMetaController() {
        return metaControllers;
    }
}
