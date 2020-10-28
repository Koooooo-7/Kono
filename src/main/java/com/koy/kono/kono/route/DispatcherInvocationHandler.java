package com.koy.kono.kono.route;

import com.koy.kono.kono.interceptor.InterceptorExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description the invocation with will solve the interceptors on the proxy object invoked.
 */

public class DispatcherInvocationHandler implements InvocationHandler {

    private InterceptorExecutor executor;
    private Dispatcher handler;

    public DispatcherInvocationHandler(InterceptorExecutor executor, Dispatcher handler) {
        this.executor = executor;
        this.handler = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO interceptors
        executor.doInterceptor(args);
        return method.invoke(handler, args);
    }
}
