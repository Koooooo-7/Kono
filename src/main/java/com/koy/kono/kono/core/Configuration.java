package com.koy.kono.kono.core;

import com.koy.kono.kono.enums.Protocol;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Configuration {

    private Integer port = 9001;
    private Protocol protocol = Protocol.HTTP;
    // TODO: multi location
    private String controllerLocation = "com.koy.kono.app.controller";

    private Map<String, String> routers = new HashMap<>();

}
