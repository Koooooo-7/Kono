package com.koy.kono.kono.interceptor;

import com.koy.kono.kono.core.Configuration;
import com.koy.kono.kono.core.DefaultClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class InterceptorFactory {

    public void loadInterceptors(DefaultClassLoader classLoader, Configuration configuration) {
        Set<Class<? extends IInterceptor>> classes = classLoader.findInterceptorClasses();

        try {
            for (Class<? extends IInterceptor> clz : classes) {
                IInterceptor iInterceptor = clz.getDeclaredConstructor().newInstance();
                InterceptorExecutor.PRE.addRegisterInterceptors(iInterceptor);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

