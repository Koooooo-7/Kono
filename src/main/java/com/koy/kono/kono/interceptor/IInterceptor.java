package com.koy.kono.kono.interceptor;

import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.RequestContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface IInterceptor {

    default void preHandle(RequestContext requestContext, ControllerFactory controllerFactory) {
    }


    default void postHandle(FullHttpResponse response) {
    }
}
