package com.maldou.lightr.client.socket;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.maldou.lightr.client.socket.out.OutValue;
import com.maldou.lightr.transport.TransportObject;

public class SocketHandler extends ChannelHandlerAdapter{

    private static final Logger logger = Logger.getLogger(SocketHandler.class.getName());

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(msg instanceof TransportObject) {
    		OutValue.getInstance().addValue((TransportObject)msg);
    	}
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
