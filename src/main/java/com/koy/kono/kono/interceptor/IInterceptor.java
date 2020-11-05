package com.koy.kono.kono.interceptor;

import com.koy.kono.kono.core.RequestContext;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.function.Predicate;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface IInterceptor {

    default Predicate<RequestContext> preHandle() {
        return req -> true;
    }


    default Predicate<RequestContext> postHandle() {
        return rep -> true;
    }

    boolean isMatchPathPatterns(String url);

    default int order() {
        return 10;
    }

}
