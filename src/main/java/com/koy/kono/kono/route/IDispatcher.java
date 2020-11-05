package com.koy.kono.kono.route;

import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.RequestContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface IDispatcher {

    Dispatch dispatch(RequestContext ctx, ChannelHandlerContext channelHandlerContext, ControllerFactory handlerFactory);

    default void dispatch(RequestContext requestContext, FullHttpResponse response) {
    }


}
