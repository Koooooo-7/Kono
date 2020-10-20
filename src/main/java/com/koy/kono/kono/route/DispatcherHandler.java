package com.koy.kono.kono.route;

import com.koy.kono.kono.core.BaseController;
import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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
    public Dispatch dispatch(FullHttpRequest fullHttpRequest, ChannelHandlerContext channelHandlerContext, ControllerFactory handler) {
        Dispatch dispatch = routeParser.dispatch(fullHttpRequest, channelHandlerContext, handler);
        RouterMatch routerMatch = dispatch.getRouterMatch();
        if (RouterMatch.NOT_FOUND == routerMatch) {
            // TODO
        }

        handler.handle(dispatch.getTargetController(), dispatch.getTargetMethod());
        return null;
    }
}
