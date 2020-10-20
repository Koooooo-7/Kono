package com.koy.kono.kono.server;

import com.koy.kono.kono.core.ApplicationContext;
import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.route.DispatcherHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
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

        DispatcherHandler dispatcherHandler = applicationContext.getDispatcherHandler();
        ControllerFactory handler = applicationContext.getControllerFactory();
        dispatcherHandler.dispatch(fullHttpRequest, channelHandlerContext, handler);

    }


//        // POST
//        HttpMethod method = fullHttpRequest.method();
//
//        //
//        String uri = fullHttpRequest.uri();
//        FullHttpResponse response = new DefaultFullHttpResponse(
//                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
//                Unpooled.wrappedBuffer("11".getBytes()));
//
//        response.headers().set("Content-Type", "application/xml");
//
//        response.headers().setInt("11",
//                response.content().readableBytes());
//        System.out.println(uri);
//        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

}
