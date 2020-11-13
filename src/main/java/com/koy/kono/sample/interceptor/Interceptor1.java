package com.koy.kono.sample.interceptor;

import com.koy.kono.kono.interceptor.IInterceptor;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class Interceptor1 implements IInterceptor {

    // of cuz u can use regex to match the url pattern
    @Override
    public boolean isMatchPathPatterns(String url) {
        return url.matches("/index/*");
    }

    @Override
    public int order() {
        return 1;
    }
}
