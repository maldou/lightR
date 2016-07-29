package com.maldou.lightr.transport;

import java.io.Serializable;

public class TransportObject implements Serializable {
	
	private static final long serialVersionUID = -7379306700336789661L;
	
	private long id;
	
	private int transType;//1-请求；2-响应
	
	private Class<?> clazz;
	private Object[] parameters;
	private String methodName;
	private String lookup;
	private String requestHost;
	
	private String responseHost;
	private Object outObject;
	
	public TransportObject() {
		
	}
	
	public TransportObject(long serverId) {
		id = IdCreator.getInstance().create(serverId);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public int getTransType() {
		return transType;
	}

	public void setTransType(int transType) {
		this.transType = transType;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object getOutObject() {
		return outObject;
	}

	public void setOutObject(Object outObject) {
		this.outObject = outObject;
	}

	public String getRequestHost() {
		return requestHost;
	}

	public void setRequestHost(String requestHost) {
		this.requestHost = requestHost;
	}

	public String getResponseHost() {
		return responseHost;
	}

	public void setResponseHost(String responseHost) {
		this.responseHost = responseHost;
	}

}
