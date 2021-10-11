package com.server.core;

import com.server.core.handler.SimpleChatServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangshouren
 */
@Slf4j
public class TransportServer {
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private ServerBootstrap serverBootstrap;
    private String port = "8200";

    public void init() {
        boss = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        worker = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        run();
    }

    public void run() {
        serverBootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new SimpleChatServerInitializer());
        try {
            ChannelFuture cf = serverBootstrap.bind(Integer.parseInt(port)).sync();
            cf.channel().closeFuture().sync();
            log.info("netty启动成功...");
        } catch (InterruptedException e) {
            log.error("netty启动失败,{}", e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public void setPort(String port) {
        this.port = port;
    }


}
