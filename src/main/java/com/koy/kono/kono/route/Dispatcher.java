package com.koy.kono.kono.route;

import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.RequestContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface Dispatcher {

    Dispatch dispatch(RequestContext ctx, ChannelHandlerContext channelHandlerContext, ControllerFactory handlerFactory);

    default void dispatch(FullHttpResponse response) {
    }


}
