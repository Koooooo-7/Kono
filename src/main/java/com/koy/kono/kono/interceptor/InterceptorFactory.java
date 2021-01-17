package com.koy.kono.kono.interceptor;

import com.koy.kono.kono.core.Configuration;
import com.koy.kono.kono.core.DefaultClassLoader;
import com.koy.kono.kono.core.IFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class InterceptorFactory implements IFactory {

    @Override
    public void load(DefaultClassLoader defaultClassLoader, Configuration configuration) {
        loadInterceptors(defaultClassLoader, configuration);
    }

    private void loadInterceptors(DefaultClassLoader classLoader, Configuration configuration) {
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

