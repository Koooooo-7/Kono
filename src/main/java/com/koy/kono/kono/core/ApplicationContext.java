package com.koy.kono.kono.core;

import com.koy.kono.kono.interceptor.InterceptorExecutor;
import com.koy.kono.kono.interceptor.InterceptorFactory;
import com.koy.kono.kono.route.IDispatcher;
import com.koy.kono.kono.route.DispatcherHandler;
import com.koy.kono.kono.route.DispatcherInvocationHandler;
import com.koy.kono.kono.route.RouteParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;


/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description The whole application context, which as the container of all the framework happens.
 */

public class ApplicationContext {

    // the holder for each request context.
    private static final ThreadLocal<RequestContext> requestContext = new ThreadLocal<>();
    Supplier<RequestContext.Builder> requestContextSupplier = RequestContext.Builder::new;

    private Configuration configuration;
    private DispatcherHandler dispatcherHandler;
    private ControllerFactory controllerFactory;

    public ApplicationContext(Configuration configuration) {
        this.configuration = configuration;
    }

    // load application
    public void refresh() {
        // load configuration
        DefaultClassLoader classLoader = new DefaultClassLoader(configuration);

        BootsListener bootsListener = new BootsListener(this);
        bootsListener.notifyListener();
        // load controller meta data
        controllerFactory = new ControllerFactory();
        controllerFactory.load(classLoader, configuration);

        // load interceptor
        IFactory interceptorFactory = new InterceptorFactory();
        interceptorFactory.load(classLoader, configuration);

        // initial router parser that register all the controller meta
        RouteParser routeParser = new RouteParser(controllerFactory);
        // register route parser
        dispatcherHandler = new DispatcherHandler(routeParser, this);
        // print banner
        printBanner();
    }

    private void printBanner() {
        ApplicationBannerSprinter bannerSprinter = new ApplicationBannerSprinter(configuration);
        bannerSprinter.print();
    }

    public void in(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {

        // generate request context
        RequestContext requestContext = requestContextSupplier.get()
                .setRequest(fullHttpRequest, this)
                .setRequestUrl(fullHttpRequest.uri())
                .setRequestMethodType(fullHttpRequest.method())
                .build();

        // set to the request context holder
        this.setConfigurationRequestContext(requestContext);

        // dispatch
        IDispatcher dispatcherHandler = this.getDispatcherHandler(InterceptorExecutor.PRE);
        ControllerFactory handlerFactory = this.getControllerFactory();
        dispatcherHandler.dispatch(requestContext, channelHandlerContext, handlerFactory);
    }

    // return the request and remove the request context from holder
    public void out(FullHttpResponse response) {
        IDispatcher dispatcherHandler = this.getDispatcherHandler(InterceptorExecutor.POST);
        dispatcherHandler.dispatch(requestContext.get(), response);
        this.removeRequestContext();
    }

    private ControllerFactory getControllerFactory() {
        return controllerFactory;
    }

    public DispatcherHandler getDispatcherHandler() {
        return dispatcherHandler;
    }

    // return the proxy object, which keep the InterceptorExecutor.
    private IDispatcher getDispatcherHandler(InterceptorExecutor executor) {
        return (IDispatcher) Proxy.newProxyInstance(DefaultClassLoader.getDefaultClassLoader()
                , new Class[]{IDispatcher.class}
                , new DispatcherInvocationHandler(executor, this.dispatcherHandler));
    }

    private void setConfigurationRequestContext(RequestContext ctx) {
        requestContext.set(ctx);
    }

    public void removeRequestContext() {
        requestContext.remove();
    }


    private static class ApplicationBannerSprinter {

        public static final String BANNER_LOCATION = "banner.kono";
        private Configuration configuration;

        public ApplicationBannerSprinter(Configuration configuration) {
            this.configuration = configuration;
        }

        public void print() {
            if (configuration.getShowBanner()){
                Banner banner = getBanner();
                banner.printBanner();
            }
        }

        private Banner getBanner() {
            ClassLoader classLoader = DefaultClassLoader.getDefaultClassLoader();
            return new Banner(BANNER_LOCATION, classLoader, System.out);
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
