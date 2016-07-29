package com.maldou.lightr.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ASocket {
	
	private String host;
	private int port;
	
	private Channel channel;
	private EventLoopGroup group;
	
	public ASocket(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public Channel start() throws Exception{
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
         .channel(NioSocketChannel.class)
         .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new ObjectEncoder(),
                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                        new SocketHandler());
            }
         });
        ChannelFuture channelFuture = b.connect(host, port).sync();
        channel = channelFuture.channel();
        return channel;
	}
	
	public void stop() {
		if(channel != null) {
			try {
				channel.closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if(group != null) {
					group.shutdownGracefully();
				}
	        }   
		}
	}
	
	public void writeAndFlush(Object msg) {
		channel.writeAndFlush(msg);
	}

}
