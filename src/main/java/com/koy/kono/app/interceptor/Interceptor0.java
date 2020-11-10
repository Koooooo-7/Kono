package com.koy.kono.app.interceptor;

import com.koy.kono.kono.core.RequestContext;
import com.koy.kono.kono.interceptor.IInterceptor;

import java.util.function.Predicate;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class Interceptor0 implements IInterceptor {

    @Override
    public Predicate<RequestContext> preHandle() {
        return e -> {
            // do something here to the request context
            return true;
        };
    }

    @Override
    public boolean isMatchPathPatterns(String url) {
        return url.equalsIgnoreCase("/demo");
    }

    @Override
    public int order() {
        return 0;
    }
}
