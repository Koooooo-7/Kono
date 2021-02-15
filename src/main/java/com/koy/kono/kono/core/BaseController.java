package com.koy.kono.kono.core;

import com.koy.kono.kono.core.annotation.KonoMethod;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description The base controller that which handle the request context , hence other controllers can get it.
 */

public abstract class BaseController {

    private RequestContext requestContext;

//    private BaseController missController = new DefaultMissController();
    // default controller base route
    public final String getBaseRoute() {
        return this.getClass().getSimpleName();
    }

//    // TODO: on miss response
//    @KonoMethod
//    private void miss() {
//        missController.miss();
//    }

    public RequestContext getRequest() {
        return requestContext;
    }

    // Injection method
    @KonoMethod
    private void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }


    public RequestContext getResponse() {
        return requestContext;
    }

//    public void setMissController(BaseController controller){
//        this.missController = controller;
//    }
}
