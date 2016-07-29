package com.maldou.lightr.server.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SocketServer {
	
	private int port;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ServerBootstrap serverBootstrap;
	private Channel channel;
	
	public SocketServer(int port) {
		this.port = port;
	}
	
	public void start() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
         .channel(NioServerSocketChannel.class)
         .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new ObjectEncoder(),
                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                        new SocketHandler());
            }
         });

        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        channel = channelFuture.channel();
	}
	
	public void stop() {
		if(channel != null) {
			try {
				channel.closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if(bossGroup != null) {
					bossGroup.shutdownGracefully();
				}
				if(workerGroup != null) {
					workerGroup.shutdownGracefully();
				}
	        }   
		}
	}

}
