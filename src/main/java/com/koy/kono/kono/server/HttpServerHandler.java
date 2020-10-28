package com.koy.kono.kono.server;

import com.koy.kono.kono.core.ApplicationContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;


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
        // the input of request.
        applicationContext.in(channelHandlerContext, fullHttpRequest);

    }

}
