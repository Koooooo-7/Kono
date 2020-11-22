package com.koy.kono.kono.route;

import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description the dispatch which will store the basic match router meta data.
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

    public ControllerFactory getHandler() {
        return handler;
    }

    public void setHandler(ControllerFactory handler) {
        this.handler = handler;
    }

    public MetaController getTargetController() {
        return targetController;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

}
