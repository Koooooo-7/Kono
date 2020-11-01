package com.koy.kono.kono.route;

import com.koy.kono.kono.core.*;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description the dispatcher handler will dispatch the request and the response.
 */

public class DispatcherHandler implements Dispatcher {

    private RouteParser routeParser;
    private ApplicationContext applicationContext;
    private ChannelHandlerContext channelHandlerContext;

    public DispatcherHandler(RouteParser routeParser, ApplicationContext ctx) {
        this.routeParser = routeParser;
        this.applicationContext = ctx;
    }

    @Override
    public Dispatch dispatch(RequestContext ctx, ChannelHandlerContext channelHandlerContext, ControllerFactory handlerFactory) {
        this.channelHandlerContext = channelHandlerContext;

        Dispatch dispatch = routeParser.dispatch(ctx, channelHandlerContext, handlerFactory);
        RouterMatch routerMatch = dispatch.getRouterMatch();
        if (RouterMatch.FOUND != routerMatch) {
            // TODO redirect to miss controller
            dispatch(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
            // finished
            return null;
        }

        MetaController targetController = dispatch.getTargetController();
        injectionRequestContext(ctx, targetController);

        handlerFactory.handle(targetController, dispatch.getTargetMethod());
        // finished
        return null;
    }

    // inject the request context to BaseController that make every register controller can get the request context
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

    @Override
    public void dispatch(FullHttpResponse response) {
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
