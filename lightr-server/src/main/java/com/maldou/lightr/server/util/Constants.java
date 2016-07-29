package com.maldou.lightr.server.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Constants {
	private static Logger logger = Logger.getLogger(Constants.class);
	
	private static String confFileName = "globalconfig.properties";
	private static String serviceConfFileName = "serviceconf.properties";
	
	public static String zkConStr;
	public static int zkSessionTimeOut;
	public static String zkRoot;
	
	public static String serviceHost;
	public static int servicePort;
	
	static {
		Properties properties = new Properties();
		InputStream in = null;
		try {
			String confPath = GlobalParameter.getInstance().getConfPath();
			in = new FileInputStream(confPath + confFileName);
			properties.load(in);
			zkConStr = properties.getProperty("zk_connectString");
			zkSessionTimeOut = Integer.valueOf(properties.getProperty("zk_sessionTimeout"));
			zkRoot = properties.getProperty("zk_root");
			
			in.close();
			
			confPath = GlobalParameter.getInstance().getServiceConfigPath();
			in = new FileInputStream(confPath + serviceConfFileName);
			properties.load(in);
			serviceHost = properties.getProperty("service_host");
			servicePort = Integer.valueOf(properties.getProperty("service_port"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	


}
