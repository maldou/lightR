package com.maldou.lightr.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

public class ProxyFactory {
	
	private static Map<String, Object> proxyCache = new HashMap<String, Object>();
	private static Object lock = new Object();
	
	private ProxyFactory() {
		
	}
	
	private static <T> T create(String url, Class<T> type) throws Exception{
		Object proxy = proxyCache.get(url);
		if(proxy == null) {
			proxy = createProxy(url, type);
			if(proxy != null) {
				proxyCache.put(url, proxy);
			}
		}
		return (T) proxy;
	}
	
	/**
	 * 
	 * @param url格式：bus://myservice/busservice
	 * @return
	 */
	public static Object createProxy(String url, Class<?> type) throws Exception{
		if(Strings.isNullOrEmpty(url)) {
			throw new Exception("url is null");
		}
		if(!url.startsWith("bus://")) {
			throw new Exception("url格式错误!");
		}
		url = url.replaceAll("bus://", "");
		String[] array = url.split("/");
		String serviceName = array[0];
		String lookup = array[1];
		
		InvocationHandler handler = new InvocationHandlerImpl(serviceName, lookup);
		
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {type}, handler);
	}

}
