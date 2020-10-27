package com.koy.kono.kono.core;

import java.lang.reflect.Method;

public interface IController {
    void handle(MetaController handler, Method method);

}
