package com.koy.kono.kono.route;

import com.koy.kono.kono.core.ControllerFactory;
import com.koy.kono.kono.core.MetaController;
import com.koy.kono.kono.enums.RouterMatch;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

@Data
@AllArgsConstructor
public class Dispatch {

    private RouterMatch routerMatch;
    private ChannelHandlerContext channelHandlerContext;
    private ControllerFactory handler;
    private MetaController targetController;
    private Method targetMethod;

}
