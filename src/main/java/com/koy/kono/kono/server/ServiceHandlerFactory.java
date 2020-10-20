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

    static void getHandler(ChannelPipeline p, Configuration configuration) {
        switch (configuration.getProtocol()) {
            case HTTP:
                ApplicationContext applicationContext = startApplication(configuration);
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


    private static ApplicationContext startApplication(Configuration configuration) {
        ApplicationContext applicationContext = new ApplicationContext(configuration);
        applicationContext.refresh();
        return applicationContext;
    }

}
