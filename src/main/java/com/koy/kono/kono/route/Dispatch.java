package com.koy.kono.kono.route;

import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class Dispatch {

    private RouterMatch routerMatch;
    private ChannelHandlerContext channelHandlerContext;
    private ControllerFactory handler;
    private MetaController targetController;
    private Method targetMethod;

    public Dispatch(RouterMatch routerMatch, ChannelHandlerContext channelHandlerContext, ControllerFactory handler, MetaController targetController, Method targetMethod) {
        this.routerMatch = routerMatch;
        this.channelHandlerContext = channelHandlerContext;
        this.handler = handler;
        this.targetController = targetController;
        this.targetMethod = targetMethod;
    }

    public RouterMatch getRouterMatch() {
        return routerMatch;
    }

    public void setRouterMatch(RouterMatch routerMatch) {
        this.routerMatch = routerMatch;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public ControllerFactory getHandler() {
        return handler;
    }

    public void setHandler(ControllerFactory handler) {
        this.handler = handler;
    }

    public MetaController getTargetController() {
        return targetController;
    }

    public void setTargetController(MetaController targetController) {
        this.targetController = targetController;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }
}
