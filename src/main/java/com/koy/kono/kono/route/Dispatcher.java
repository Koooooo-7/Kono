package com.koy.kono.kono.route;

import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.RequestContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface Dispatcher {

    Dispatch dispatch(RequestContext ctx, ChannelHandlerContext channelHandlerContext, ControllerFactory handler);

}
