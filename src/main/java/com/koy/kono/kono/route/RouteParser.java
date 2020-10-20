package com.koy.kono.kono.route;

import com.koy.kono.kono.core.BaseController;
import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
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
    public Dispatch dispatch(FullHttpRequest fullHttpRequest, ChannelHandlerContext channelHandlerContext, ControllerFactory handler) {

        String url = fullHttpRequest.uri();
        int first = url.indexOf('/');
        String route = url.substring(first);
        int second = route.indexOf('/');
        String controller = route.substring(0, second);
        int last = route.indexOf('?');
        String method = route.substring(second + 1, last);

        Router router = routers.get(controller);
        if (Objects.isNull(router)) {
            // TODO
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
            if ("miss".equals(matchMethod.getName()) && Objects.nonNull(matchMethod.getAnnotation(KonoMethod.class))) {
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
                    .filter(method -> methodName.equals(method.getName()))
                    .findFirst()
                    .orElseGet(() -> {
                        try {
                            return BaseController.class.getMethod("miss");
                        } catch (NoSuchMethodException ignore) {
                        }
                        return null;
                    });
        }

    }

    private void addRouter(String baseRouter, Router router) {
        routers.put(baseRouter, router);
    }
}
