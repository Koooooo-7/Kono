package com.koy.kono.kono.core;

import com.koy.kono.kono.route.DispatcherHandler;
import com.koy.kono.kono.route.RouteParser;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
        dispatcherHandler = new DispatcherHandler(routeParser, this);
        printBanner();
    }

    private void printBanner() {
        ApplicationBannerSprinter bannerSprinter = new ApplicationBannerSprinter(configuration);
        bannerSprinter.print();
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
        }).map(Map.Entry::getValue).findFirst().orElseGet(()->"");

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
        metaControllers.put(name, metaController);
    }

    private Map<String, MetaController> getMetaController() {
        return metaControllers;
    }

    public void setConfigurationRequestContext(RequestContext ctx) {
        requestContext.set(ctx);
    }

    public void removeRequestContext() {
        requestContext.remove();
    }


    private static class ApplicationBannerSprinter {

        public static final String BANNER_LOCATION_FILE = "banner.kono";
        private Configuration configuration;

        public ApplicationBannerSprinter(Configuration configuration) {
            this.configuration = configuration;
        }

        public void print() {
            // TODO: whether print banner
            Banner banner = getBanner();
            banner.printBanner();
        }

        private Banner getBanner() {
            ClassLoader classLoader = ControllerClassLoader.getDefaultClassLoader();
            return new Banner(BANNER_LOCATION_FILE, classLoader, System.out);
        }

    }

    private static class Banner {

        private String resource;
        private ClassLoader classLoader;
        private PrintStream printStream;

        public Banner(String resource, ClassLoader classLoader, PrintStream printStream) {
            this.resource = resource;
            this.classLoader = classLoader;
            this.printStream = printStream;
        }

        private void printBanner() {
            try {
                InputStream resourceAsStream = classLoader.getResourceAsStream(resource);
                String bannerString = copyToString(resourceAsStream);
                printStream.print(bannerString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String copyToString(InputStream in) throws IOException {

        final int BUFFER_SIZE = 4096;

        if (in == null) {
            return "No banner can be found :(";
        }

        StringBuilder out = new StringBuilder(BUFFER_SIZE);
        InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        char[] buffer = new char[BUFFER_SIZE];
        int charsRead;
        while ((charsRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, charsRead);
        }
        return out.toString();
    }
}
