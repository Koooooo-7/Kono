package com.koy.kono.app.interceptor;

import com.koy.kono.kono.core.RequestContext;
import com.koy.kono.kono.interceptor.IInterceptor;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.function.Predicate;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class Interceptor2 implements IInterceptor {

    @Override
    public Predicate<RequestContext> postHandle() {
        return e -> {
            // change the response info
            e.getResponse().setStatus(HttpResponseStatus.BAD_REQUEST);
            e.setContent(Unpooled.wrappedBuffer("New Response".getBytes()));
            return false;
        };
    }

    @Override
    public boolean isMatchPathPatterns(String url) {

        return url.matches("(?i)/index/user");
    }

    @Override
    public int order() {
        return 2;
    }
}
