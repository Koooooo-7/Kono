package com.koy.kono.kono.server;

import com.koy.kono.kono.core.ApplicationContext;
import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.RequestContext;
import com.koy.kono.kono.route.Dispatch;
import com.koy.kono.kono.route.Dispatcher;
import com.koy.kono.kono.route.DispatcherHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.function.Supplier;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private ApplicationContext applicationContext;

    public HttpServerHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {


        applicationContext.in(channelHandlerContext, fullHttpRequest);

    }

}
