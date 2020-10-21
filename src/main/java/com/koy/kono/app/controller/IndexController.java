package com.koy.kono.app.controller;

import com.koy.kono.kono.core.BaseController;

import java.util.*;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class IndexController extends BaseController {

    public void user() {
        Optional<String> user = this.getRequest().get("user");
        Map<String, Object> map = new HashMap<>();
        map.put("Koy", "Hello World!");
        map.put(user.orElseGet(() -> "Anonymous"), "Kono!");
        this.getResponse().send(map).json();
    }
}
