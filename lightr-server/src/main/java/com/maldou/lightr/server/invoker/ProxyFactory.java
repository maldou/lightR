package com.maldou.lightr.server.invoker;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import org.apache.log4j.Logger;

import com.maldou.lightr.server.Annocations.BusSerivce;
import com.maldou.lightr.server.classutil.DynamicClassLoader;

public class ProxyFactory {
	
	private static Logger logger = Logger.getLogger(ProxyFactory.class);
	
	public static Map<String, ServiceInvoker> proxyMap = new ConcurrentHashMap<String, ServiceInvoker>();
	
	public static ServiceInvoker getProxy(String serviceName) {
		return proxyMap.get(serviceName);
	}
	
	public static void createProxy(String className, ClassLoader classLoader) {
		try {
			Class<?> clazz = classLoader.loadClass(className);
			BusSerivce sa = clazz.getAnnotation(BusSerivce.class);
			if(sa != null) {
				logger.info("creatproxy:" + className);
				ClassPool pool = ClassPool.getDefault();
				String proxyClassName = className + "Stub";
				CtClass proxyCtClass = pool.makeClass(proxyClassName);
				String interfaceClassName = ServiceInvoker.class.getName();
				CtClass interfaceCtClass = pool.getCtClass(interfaceClassName);
				proxyCtClass.addInterface(interfaceCtClass);
				logger.info(proxyClassName);
				logger.info(interfaceClassName);
				StringBuilder fieldBuilder = new StringBuilder();
				fieldBuilder.append("private static ").append(className).append(" service = new ")
				            .append(className).append("();");
				logger.info(fieldBuilder);
				CtField proxyCtField = CtField.make(fieldBuilder.toString(), proxyCtClass);
				proxyCtClass.addField(proxyCtField);
				
				Method[] methods = clazz.getDeclaredMethods();
				for(Method method : methods) {
					String methodName = method.getName();
					Class<?> returnType = method.getReturnType();
					String returnTypeName = returnType.getCanonicalName();
					Class<?>[] parameterTypes = method.getParameterTypes();
					int parameterCount = parameterTypes.length;
					String[] parameterTypeNames = new String[parameterCount];
					for(int i = 0; i < parameterCount; i++) {
						parameterTypeNames[i] = parameterTypes[i].getCanonicalName();
					}
					String[] parameterNames = new String[parameterCount];
					for(int i = 0; i < parameterCount; i++) {
						parameterNames[i] = "arg" + i;
					}
					
					StringBuilder methodBuilder = new StringBuilder();
					methodBuilder.append("public ").append(returnTypeName).append(" ").append(methodName).append("(");
					for(int i = 0; i < parameterCount; i++) {
						methodBuilder.append(parameterTypeNames[i]).append(" ").append(parameterNames[i]);
						if(i != parameterCount - 1) {
							methodBuilder.append(",");
						}
					}
					methodBuilder.append(") {");
					methodBuilder.append(" return service.").append(methodName).append("(");
					for(int i = 0; i < parameterCount; i++) {
						methodBuilder.append(parameterNames[i]);
						if(i != parameterCount - 1) {
							methodBuilder.append(",");
						}
					}
					methodBuilder.append(");}");
					logger.info(methodBuilder);
					CtMethod proxyCtMethod = CtMethod.make(methodBuilder.toString(), proxyCtClass);
					proxyCtClass.addMethod(proxyCtMethod);
				}
				
				StringBuilder methodCallBuilder = new StringBuilder();
				methodCallBuilder.append("public java.lang.Object methodCall(java.lang.String methodName,java.lang.Object[] args) throws java.lang.Exception {");
				methodCallBuilder.append("java.lang.reflect.Method[] methods = this.getClass().getMethods();");
				methodCallBuilder.append("for(int i=0;i<methods.length;i++){");
				methodCallBuilder.append("java.lang.reflect.Method method=methods[i];");
				methodCallBuilder.append("if(method.getName().equals(methodName)) {");
				methodCallBuilder.append("return method.invoke(this, args);}");
				methodCallBuilder.append("}");
				methodCallBuilder.append("return null;}");
				logger.info(methodCallBuilder);
				CtMethod proxyCtMethod = CtMethod.make(methodCallBuilder.toString(), proxyCtClass);
				proxyCtClass.addMethod(proxyCtMethod);
				
				byte[] classByte = proxyCtClass.toBytecode();
				DynamicClassLoader clazzLoader = (DynamicClassLoader)classLoader;
				Class<?> serviceInvokerClass = clazzLoader.findClass(proxyClassName, classByte, null);
				ServiceInvoker serviceInvoker = (ServiceInvoker)serviceInvokerClass.newInstance();
				int index = className.lastIndexOf(".");
				String serviceName = index > 0 ? className.substring(index + 1) : className;
				proxyMap.put(serviceName, serviceInvoker);

			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
