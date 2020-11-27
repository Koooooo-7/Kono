package com.koy.kono.kono.core;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description the miss controller when no route matched
 */

public class DefaultMissController extends BaseController {

    public void miss(){
         this.getRequest().send("nothing found").json();
    }

}
