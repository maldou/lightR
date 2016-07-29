package com.maldou.lightr.server.util;

import com.google.common.base.Strings;

public class GlobalParameter {
	
	private GlobalParameter() {
		
	}
	private static GlobalParameter instance = new GlobalParameter();
	public static GlobalParameter getInstance() {
		return instance;
	}
	
	private String rootPath; //根目录
	
	private String serviceName; //服务名称
	private String serviceFolderPath; //服务部署路径
	private String[] serviceClassPath; //服务classpath
	private String serviceClasses; //服务的类路径
	
	private String[] libPath; //所有的lib路径
	
	private String confPath; //全局配置文件路径
	
	private String serviceConfigPath; //服务配置文件路径
	
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceFolderPath() {
		if(Strings.isNullOrEmpty(serviceFolderPath)) {
			serviceFolderPath = rootPath + "services/" + serviceName + "/";
		}
		return serviceFolderPath;
	}
	public void setServiceFolderPath(String serviceFolderPath) {
		this.serviceFolderPath = serviceFolderPath;
	}
	public String[] getServiceClassPath() {
		if(serviceClassPath == null || serviceClassPath.length == 0) {
			serviceClassPath = new String[]{serviceFolderPath + "classes/", serviceFolderPath + "lib/"};
		}
		return serviceClassPath;
	}
	public void setServiceClassPath(String[] serviceClassPath) {
		this.serviceClassPath = serviceClassPath;
	}
	public String getServiceClasses() {
		if(Strings.isNullOrEmpty(serviceClasses)) {
			serviceClasses = serviceFolderPath + "classes/";
		}
		return serviceClasses;
	}
	public void setServiceClasses(String serviceClasses) {
		this.serviceClasses = serviceClasses;
	}
	public String[] getLibPath() {
		if(libPath == null || libPath.length == 0) {
			libPath = new String[]{rootPath + "lib/", serviceFolderPath + "lib/"};
		}
		return libPath;
	}
	public void setLibPath(String[] libPath) {
		this.libPath = libPath;
	}
	public String getConfPath() {
		if(Strings.isNullOrEmpty(confPath)) {
			confPath = rootPath + "conf/";
		}
		return confPath;
	}
	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}
	public String getServiceConfigPath() {
		if(Strings.isNullOrEmpty(serviceConfigPath)) {
			serviceConfigPath = serviceFolderPath + "config/";
		}
		return serviceConfigPath;
	}
	public void setServiceConfigPath(String serviceConfigPath) {
		this.serviceConfigPath = serviceConfigPath;
	}
}
