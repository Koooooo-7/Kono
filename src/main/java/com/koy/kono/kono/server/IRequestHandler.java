package com.koy.kono.kono.server;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface IRequestHandler {

     Object handle(FullHttpRequest request);
}
