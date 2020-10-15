package com.koy.kono.kono.core;

import com.koy.kono.kono.enums.Protocol;
import lombok.Data;

@Data
public class Configuration {

    private Integer port = 9001;
    private Protocol protocol = Protocol.HTTP;

}
