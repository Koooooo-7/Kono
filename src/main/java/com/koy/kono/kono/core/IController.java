package com.koy.kono.kono.core;

import java.lang.reflect.Method;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface IController {
    void handle(MetaController handler, Method method);

}
