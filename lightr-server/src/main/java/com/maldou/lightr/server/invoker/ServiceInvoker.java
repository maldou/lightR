package com.maldou.lightr.server.invoker;

import java.lang.reflect.Method;

public interface ServiceInvoker {
	
	public Object methodCall(String methodName, Object[] args) throws Exception;
	
//	public Object methodCall(String methodName, Object[] args) throws Exception {
//		Method[] methods = this.getClass().getMethods();
//		for(Method method : methods) {
//			if(method.getName().equals(methodName)) {
//				return method.invoke(this, args);
//			}
//		}
//		return null;
//	}

}
