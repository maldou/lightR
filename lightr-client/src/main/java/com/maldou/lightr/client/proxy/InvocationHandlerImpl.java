package com.maldou.lightr.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.maldou.lightr.client.ClientTest;
import com.maldou.lightr.client.socket.Sockets;
import com.maldou.lightr.transport.TransportObject;

public class InvocationHandlerImpl implements InvocationHandler{
	
	private String serviceName;
	private String lookup;
	
	public InvocationHandlerImpl(String serviceName, String lookup) {
		this.serviceName = serviceName;
		this.lookup = lookup;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		TransportObject transport = new TransportObject(1);
		transport.setTransType(1);
		transport.setClazz(method.getDeclaringClass());
		transport.setMethodName(method.getName());
		transport.setParameters(args);
		transport.setLookup(lookup);
		return Sockets.getInstance().syncRequest(serviceName, transport);
	}

}
