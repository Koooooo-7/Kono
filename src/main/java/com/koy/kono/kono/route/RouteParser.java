package com.koy.kono.kono.route;

import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
import com.koy.kono.kono.core.RequestContext;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description The route parser which parse the route and get the target Dispatch.
 */

public class RouteParser implements IDispatcher {

    private static final ConcurrentHashMap<String, Router> routers = new ConcurrentHashMap<>();

    @Override
    public Dispatch dispatch(RequestContext ctx, ChannelHandlerContext channelHandlerContext, ControllerFactory handler) {

        Router matchRouter = findRouter(ctx.getRequest());
        if (matchRouter.getRouterMatch() != RouterMatch.FOUND) {
            // TODO: redirect to miss controller
            return new Dispatch(matchRouter.getRouterMatch(), channelHandlerContext, handler, null, null);
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

    public static class Router {

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

        public Router getMatchRouter(final String requestType, final String methodName) {
            MetaController.MetaMethod matchMetaMethod = getMatchMetaMethod(requestType, methodName);

            if (Objects.isNull(matchMetaMethod) || !matchMetaMethod.isAccess(requestType)) {
                routerMatch = RouterMatch.NOT_FOUND;
                return this;
            }

            Method matchMethod = matchMetaMethod.getMethod();
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

        // find the matched meta method, the matched rules have different priority
        private MetaController.MetaMethod getMatchMetaMethod(String requestType, String methodName) {

            Map<Boolean, List<MetaController.MetaMethod>> matchedMethodMap = metaController.getMethods()
                    .parallelStream()
                    .filter(metaMethod -> (requestType + methodName).equalsIgnoreCase(metaMethod.getMethod().getName())
                            || methodName.equalsIgnoreCase(metaMethod.getMethod().getName()))
                    .collect(Collectors.partitioningBy(e -> e.isMatchedAccess(requestType)));

            // no match
            if (matchedMethodMap.isEmpty()) {
                return null;
            }

            // match the method not request type
            if (matchedMethodMap.get(Boolean.TRUE).isEmpty()) {
                return matchedMethodMap.get(Boolean.FALSE).isEmpty() ? null : matchedMethodMap.get(Boolean.FALSE).get(0);
            }

            // full match
            return matchedMethodMap.get(Boolean.TRUE).isEmpty() ? null : matchedMethodMap.get(Boolean.TRUE).get(0);

        }

    }


    // parse the route from the request url
    private Router findRouter(FullHttpRequest fullHttpRequest) {

        String url = fullHttpRequest.uri();
        String requestType = fullHttpRequest.method().name();

        // TODO: it is only support GET/POST request right now
        if (!"GET".equalsIgnoreCase(requestType) && !"POST".equalsIgnoreCase(requestType)) {
            return new Router(RouterMatch.NOT_FOUND, null, null);
        }

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

        return router.getMatchRouter(requestType, method);
    }

    private void addRouter(String baseRouter, Router router) {
        routers.put(baseRouter.toLowerCase(), router);
    }
}
