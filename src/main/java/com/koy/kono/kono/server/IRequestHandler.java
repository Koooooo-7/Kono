package com.koy.kono.kono.server;

import io.netty.handler.codec.http.FullHttpRequest;

public interface IRequestHandler {

     Object handle(FullHttpRequest request);
}
