package com.koy.kono.kono.route;

import com.koy.kono.kono.core.ControllerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface Dispatcher {

    Dispatch dispatch(FullHttpRequest fullHttpRequest, ChannelHandlerContext channelHandlerContext, ControllerFactory handler);

}
