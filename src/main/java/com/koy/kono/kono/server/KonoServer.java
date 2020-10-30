package com.koy.kono.kono.server;

import com.koy.kono.kono.core.ApplicationContext;
import com.koy.kono.kono.core.Configuration;
import com.koy.kono.kono.core.ConfigurationLoader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class KonoServer {

    public void run() {
        // get config
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.configuration();
        ApplicationContext applicationContext = startApplication(configuration);
        // run container
        this.run(applicationContext, configuration);
    }

    private ApplicationContext startApplication(Configuration configuration) {
        ApplicationContext applicationContext = new ApplicationContext(configuration);
        applicationContext.refresh();
        return applicationContext;
    }

    public void run(ApplicationContext applicationContext, Configuration configuration) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // Open Nagle
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // Open TCP heart-beat
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // Cache client
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline p = socketChannel.pipeline();
                            // Handler
                            ServiceHandlerFactory.getHandler(p, applicationContext, configuration);

                        }
                    });
            ChannelFuture sync = bootstrap.bind(configuration.getPort()).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
