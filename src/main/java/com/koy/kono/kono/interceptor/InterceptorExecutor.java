package com.koy.kono.kono.interceptor;


import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.RequestContext;
import io.netty.handler.codec.http.FullHttpResponse;
import javafx.scene.chart.ScatterChart;

import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public enum InterceptorExecutor {
    PRE, POST;

    private LinkedList<IInterceptor> interceptors;

    public InterceptorExecutor addInterceptor(LinkedList<IInterceptor> interceptors) {
        this.interceptors = interceptors;
        return this;
    }

    public void doInterceptor(Object[] args) {
        if (Objects.isNull(this.interceptors) || this.interceptors.isEmpty()) {
            return;
        }
        switch (this) {
            case PRE:
                interceptors.forEach(interceptor -> interceptor.preHandle((RequestContext) args[0], (ControllerFactory) args[2]));
                break;
            case POST:
                interceptors.forEach(interceptor -> interceptor.postHandle((FullHttpResponse) args[0]));
                break;
            default:
                break;
        }
    }

}
