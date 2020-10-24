package com.koy.kono.kono.core;

import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class MetaController {

    private Object instance;
    private List<MetaMethod> methods;
    private String baseRouter;

    public MetaController(Object instance, List<MetaMethod> methods, String baseRouter) {
        this.instance = instance;
        this.methods = methods;
        this.baseRouter = baseRouter;
    }

    public Object getInstance() {
        return instance;
    }

    public List<MetaMethod> getMethods() {
        return methods;
    }

    public static class MetaMethod {
        enum MethodAccess {
            ALL, GET, POST;
        }

        private Method method;
        private MethodAccess methodAccess;

        public MetaMethod(Method method, MethodAccess methodAccess) {
            this.method = method;
            this.methodAccess = methodAccess;
        }

        public Method getMethod() {
            return method;
        }

        public MethodAccess getMethodAccess() {
            return methodAccess;
        }

        public static MethodAccess getMethodAccess(String method) {
            if (method.startsWith("get")) {
                return MethodAccess.GET;
            }

            if (method.startsWith("post")) {
                return MethodAccess.POST;
            }
            return MethodAccess.ALL;
        }

        public boolean isAccess(String requestType) {
            if (this.methodAccess == MethodAccess.ALL) {
                return true;
            }
            return requestType.equalsIgnoreCase(this.methodAccess.name());

        }

        public boolean isMatchedAccess(String requestType) {
            return requestType.equalsIgnoreCase(this.methodAccess.name());
        }
    }

    public String getBaseRouter() {
        return baseRouter;
    }

}
