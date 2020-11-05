package com.koy.kono.kono.route;

import com.koy.kono.kono.core.RequestContext;
import com.koy.kono.kono.interceptor.InterceptorExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description the invocation with will solve the interceptors on the proxy object invoked.
 */

public class DispatcherInvocationHandler implements InvocationHandler {

    private InterceptorExecutor executor;
    private IDispatcher handler;

    public DispatcherInvocationHandler(InterceptorExecutor executor, IDispatcher handler) {
        this.executor = executor;
        this.handler = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO interceptors
        boolean interceptor = executor.doInterceptor(args);
        // TODO: fix me
        if (!interceptor) {
            handler.dispatch((RequestContext) args[0], ((RequestContext) args[0]).getResponse());
            return null;
        }
        return method.invoke(handler, args);
    }
}
