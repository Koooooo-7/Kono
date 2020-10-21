package com.koy.kono.kono.route;

import com.koy.kono.kono.core.BaseController;
import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
import com.koy.kono.kono.core.RequestContext;
import com.koy.kono.kono.core.annotation.KonoMethod;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RouteParser implements Dispatcher {

    private static final ConcurrentHashMap<String, Router> routers = new ConcurrentHashMap<>();

    @Override
    public Dispatch dispatch(RequestContext ctx, ChannelHandlerContext channelHandlerContext, ControllerFactory handler) {

        FullHttpRequest fullHttpRequest = ctx.getRequest();
        String url = fullHttpRequest.uri();
        int methodIndex = url.lastIndexOf('/');
        int paramsIndex = url.indexOf('?');
        String controllerRoute = url.substring(0, methodIndex);
        String method = url.substring(methodIndex + 1, paramsIndex == -1 ? url.length() - 1 : paramsIndex);

        Router router = routers.get(controllerRoute.toLowerCase());
        if (Objects.isNull(router)) {
            // TODO
            return null;
//            return new Dispatch(RouterMatch.NOT_FOUND, channelHandlerContext, handler, null, null);
        }
        Router matchRouter = router.getMatchRouter(method);
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

    private class Router {

        private String baseRoute;
        private MetaController metaController;
        private RouterMatch routerMatch;
        private Method matchedMethod;

        public Router(String baseRoute, MetaController metaController) {
            this.baseRoute = baseRoute;
            this.metaController = metaController;
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

    private void addRouter(String baseRouter, Router router) {
        routers.put(baseRouter.toLowerCase(), router);
    }
}
