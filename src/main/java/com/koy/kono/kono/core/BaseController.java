package com.koy.kono.kono.core;

import com.koy.kono.kono.core.annotation.KonoMethod;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public abstract class BaseController {

    private RequestContext requestContext;

    // default controller base route
    public final String getBaseRoute() {
        return this.getClass().getSimpleName();
    }

    // TODO: on miss response
    @KonoMethod
    public String miss() {
        return "";
    }

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


}
