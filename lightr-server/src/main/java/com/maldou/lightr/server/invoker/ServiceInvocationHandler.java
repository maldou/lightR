package com.maldou.lightr.server.invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceInvocationHandler implements InvocationHandler{
	
	private ServiceInvoker serviceObject;
	
	public ServiceInvocationHandler(ServiceInvoker serviceObject) {
		this.serviceObject = serviceObject;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return method.invoke(serviceObject, args);
	}

}
