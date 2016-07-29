package com.maldou.lightr.server.socket;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.maldou.lightr.server.invoker.ProxyFactory;
import com.maldou.lightr.server.invoker.ServiceInvoker;
import com.maldou.lightr.transport.TransportObject;

public class SocketHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(SocketHandler.class.getName());

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof TransportObject) {
        	TransportObject transport = (TransportObject)msg;
        	String serviceName = transport.getLookup();
        	String methodName = transport.getMethodName();
        	logger.info("request service:" + serviceName + ",method:" + methodName);
        	Class<?> clazz = transport.getClazz();
        	Object[] parameters = transport.getParameters();
        	ServiceInvoker service = ProxyFactory.getProxy(serviceName);
        	Object result = service.methodCall(methodName, parameters);
        	if(result == null) {
        		logger.info("return null, methodname:" + methodName);
        	}
        	TransportObject response = new TransportObject();
        	response.setId(transport.getId());
        	response.setTransType(2);
        	response.setOutObject(result);
        	ctx.writeAndFlush(response);
        }
    	
    }
    
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }
    
    public void response() {
    	
    }
}
