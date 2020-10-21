package com.koy.kono.kono.server;

import com.koy.kono.kono.core.ApplicationContext;
import com.koy.kono.kono.core.Configuration;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class ServiceHandlerFactory {

    static void getHandler(ChannelPipeline p, ApplicationContext applicationContext, Configuration configuration) {
        switch (configuration.getProtocol()) {
            case HTTP:
                createHttpHandler(p, applicationContext);
                break;
            default:
                break;
        }
    }

    private static void createHttpHandler(ChannelPipeline p, ApplicationContext applicationContext) {
        p.addLast("decoder", new HttpRequestDecoder())
                .addLast("encoder", new HttpResponseEncoder())
                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                .addLast("handler", new HttpServerHandler(applicationContext));
    }


}
