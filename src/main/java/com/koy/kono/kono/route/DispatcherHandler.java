package com.koy.kono.kono.route;

import com.koy.kono.kono.core.BaseController;
import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
import com.koy.kono.kono.core.RequestContext;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class DispatcherHandler implements Dispatcher {

    private RouteParser routeParser;

    private DispatcherHandler self;

    public DispatcherHandler(RouteParser routeParser) {
        this.routeParser = routeParser;
        self = this;
    }

    @Override
    public Dispatch dispatch(RequestContext ctx, ChannelHandlerContext channelHandlerContext, ControllerFactory handler) {
        Dispatch dispatch = routeParser.dispatch(ctx, channelHandlerContext, handler);
        RouterMatch routerMatch = dispatch.getRouterMatch();
        if (RouterMatch.NOT_FOUND == routerMatch) {
            // TODO
        }

        MetaController targetController = dispatch.getTargetController();
        injectionRequestContext(ctx, targetController);

        handler.handle(targetController, dispatch.getTargetMethod());
        // finished
        return null;
    }

    private void injectionRequestContext(RequestContext ctx, MetaController targetController) {
        try {
            Object instance = targetController.getInstance();
            Method setRequestContext = BaseController.class.getDeclaredMethod("setRequestContext", RequestContext.class);
            setRequestContext.setAccessible(true);
            setRequestContext.invoke(instance, ctx);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
