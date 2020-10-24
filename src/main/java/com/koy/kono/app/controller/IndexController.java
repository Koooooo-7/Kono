package com.koy.kono.app.controller;

import com.koy.kono.kono.core.BaseController;

import java.util.*;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description Example, the default route is /index.
 */

public class IndexController extends BaseController {

    // both support the GET and POST request to /index/user
    public void user() {
        Optional<String> user = this.getRequest().get("user");
        Map<String, Object> map = new HashMap<>();
        map.put("Koy", "Hello World!");
        map.put(user.orElseGet(() -> "Anonymous"), "Kono!");
        this.getResponse().send(map).json();
    }

    // only support the GET request to /index/user, the specific request type method has higher priority.
    public void getUser(){
        Optional<String> user = this.getRequest().get("user");
        Map<String, Object> map = new HashMap<>();
        map.put("Koy2", "Hello World!2");
        map.put(user.orElseGet(() -> "Anonymous2"), "Kono!2");
        this.getResponse().send(map).json();
    }
}
