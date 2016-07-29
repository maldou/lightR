package com.maldou.lightr.example.service.impl.util;

import java.lang.reflect.Proxy;
import java.util.List;

import com.maldou.lightr.example.service.impl.service.AmapfuseServiceImpl;
import com.maldou.lightr.server.invoker.ServiceInvocationHandler;
import com.maldou.lightr.server.invoker.ServiceInvoker;

public class InvockerTest implements ServiceInvoker {

	private static AmapfuseServiceImpl service = new AmapfuseServiceImpl();

	public java.lang.String getNameById(long arg0) {
		return service.getNameById(arg0);
	}

	public List allEntities() {
		return service.allEntities();
	}

	public java.lang.Object methodCall(java.lang.String methodName,
			java.lang.Object[] args) throws java.lang.Exception {
		java.lang.reflect.Method[] methods = this.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			java.lang.reflect.Method method = methods[i];
			if (method.getName().equals(methodName)) {
				return method.invoke(this, args);
			}
		}
		System.out.println("hello.");
		return null;
	}

	public String hello() {
		System.out.println("hello!");
		return "hello.";
	}

	public static void main(String[] args) throws Exception {
		InvockerTest test = new InvockerTest();
		try {
			String result = (String) test.methodCall("hello", new Object[] {});
			System.out.println("result:" + result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ServiceInvocationHandler handler = new ServiceInvocationHandler(test);
		InvockerTest proxy = (InvockerTest) Proxy.newProxyInstance(Thread
				.currentThread().getContextClassLoader(), test.getClass()
				.getInterfaces(), handler);
		String result = (String) proxy.methodCall("hello", new Object[] {});
		System.out.println("result:" + result);
	}

}
