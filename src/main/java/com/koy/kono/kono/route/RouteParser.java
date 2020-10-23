package com.koy.kono.kono.route;

import com.koy.kono.kono.core.BaseController;
import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
import com.koy.kono.kono.core.RequestContext;
import com.koy.kono.kono.core.annotation.KonoMethod;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RouteParser implements Dispatcher {

    private static final ConcurrentHashMap<String, Router> routers = new ConcurrentHashMap<>();

    @Override
    public Dispatch dispatch(RequestContext ctx, ChannelHandlerContext channelHandlerContext, ControllerFactory handler) {

        Router matchRouter = findRouter(ctx.getRequest());
        if (matchRouter.getRouterMatch() == RouterMatch.NOT_FOUND) {
            // TODO: redirect to miss controller
            return new Dispatch(RouterMatch.NOT_FOUND, channelHandlerContext, handler, null, null);
        }
        return new Dispatch(matchRouter.getRouterMatch(), channelHandlerContext, handler, matchRouter.getMetaController(), matchRouter.getMatchedMethod());
    }

    public RouteParser(ControllerFactory controllerFactory) {
        loadRouters(controllerFactory);
    }

    private void loadRouters(ControllerFactory controllerFactory) {
        Map<String, MetaController> controllers = controllerFactory.getControllers();
        for (Map.Entry<String, MetaController> controller : controllers.entrySet()) {
            String baseUrl = controller.getKey();
            MetaController metaController = controller.getValue();
            addRouter(baseUrl, new Router(baseUrl, metaController));
        }
    }

    private static class Router {

        private String baseRoute;
        private RouterMatch routerMatch;
        private MetaController metaController;
        private Method matchedMethod;

        public Router(String baseRoute, MetaController metaController) {
            this.baseRoute = baseRoute;
            this.metaController = metaController;
        }

        public Router(RouterMatch routerMatch, MetaController metaController, Method matchedMethod) {
            this.metaController = metaController;
            this.routerMatch = routerMatch;
            this.matchedMethod = matchedMethod;
        }

        public Router getMatchRouter(final String methodName) {
            Method matchMethod = getMatchMethod(methodName);
            if ("miss".equalsIgnoreCase(matchMethod.getName()) && Objects.nonNull(matchMethod.getAnnotation(KonoMethod.class))) {
                routerMatch = RouterMatch.NOT_FOUND;
                matchedMethod = matchMethod;
                return this;
            }

            routerMatch = RouterMatch.FOUND;
            matchedMethod = matchMethod;
            return this;
        }

        public RouterMatch getRouterMatch() {
            return routerMatch;
        }

        public Method getMatchedMethod() {
            return matchedMethod;
        }

        public MetaController getMetaController() {
            return metaController;
        }

        private Method getMatchMethod(String methodName) {
            return metaController.getMethods()
                    .parallelStream()
                    .filter(method -> methodName.equalsIgnoreCase(method.getName()))
                    .findFirst()
                    .orElseGet(() -> {
                        try {
                            // TODO: abstract method can not be invoked
                            return BaseController.class.getMethod("miss");
                        } catch (NoSuchMethodException ignore) {
                        }
                        return null;
                    });
        }

    }


    private Router findRouter(FullHttpRequest fullHttpRequest) {

        String url = fullHttpRequest.uri();
        int methodIndex = url.lastIndexOf('/');
        int paramsIndex = url.indexOf('?');
        String controllerRoute = url.substring(0, methodIndex);

        // when the controller is root router, if the router is //user, it's illegal
        if ("/".equalsIgnoreCase(controllerRoute)) {
            return new Router(RouterMatch.NOT_FOUND, null, null);
        }

        // TODO: how about redirect to the /index method by default, making /user as controller. thoughts?
        // when the controller is root router, only have the method in the url and controller is empty (/user)
        if (StringUtil.isNullOrEmpty(controllerRoute)) {
            controllerRoute = "/";
        }

        Router router = routers.get(controllerRoute.toLowerCase());
        if (Objects.isNull(router)) {
            // TODO: redirect to miss controller
            return new Router(RouterMatch.NOT_FOUND, null, null);
        }
        String method = url.substring(methodIndex + 1, paramsIndex == -1 ? url.length() : paramsIndex);

        // TODO: getMethod, postMethod
        return router.getMatchRouter(method);
    }

    private void addRouter(String baseRouter, Router router) {
        routers.put(baseRouter.toLowerCase(), router);
    }
}
