package com.maldou.lightr.server.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.maldou.lightr.server.classutil.DynamicClassLoader;
import com.maldou.lightr.server.classutil.GlobalClassLoader;
import com.maldou.lightr.server.classutil.ScanClass;
import com.maldou.lightr.server.socket.SocketServer;
import com.maldou.lightr.server.util.Constants;
import com.maldou.lightr.server.util.GlobalParameter;
import com.maldou.lightr.server.zk.ZookeeperManager;

public class ServerBoot {
	
	private static Logger logger = Logger.getLogger(ServerBoot.class);
	
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			throw new IllegalArgumentException("usage: -Dservice.name=<service-name>");
		}

		String userDir = System.getProperty("user.dir");
		String rootPath = userDir + "/../";  //根目录
		logger.info("rootpath:" + rootPath);
		GlobalParameter.getInstance().setRootPath(rootPath);
		String serviceName = null;
		Map<String, String> argsMap = new HashMap<String, String>();		
		for(int i=0; i<args.length; i++) {
			if(args[i].startsWith("-D")) {
				String[] aryArg = args[i].split("=");
				if(aryArg.length == 2) {
					if(aryArg[0].equalsIgnoreCase("-Dservice.name")) {
						serviceName = aryArg[1]; //服务名称
					}
					argsMap.put(aryArg[0].replaceFirst("-D", ""), aryArg[1]);
				}
			}
		}
		
		if(serviceName == null){
			throw new Exception("no service name please set it");
		}
		logger.info("service:" + serviceName);
		GlobalParameter.getInstance().setServiceName(serviceName);		
		
		String serviceFolderPath = GlobalParameter.getInstance().getServiceFolderPath(); //服务部署路径
		logger.info("servicefolderpath:" + serviceFolderPath);
		String[] serviceClassPath = GlobalParameter.getInstance().getServiceClassPath(); //服务classpath
		
		//加载所有jar和class以及配置文件
		GlobalClassLoader.addSystemClassPathOfJar(GlobalParameter.getInstance().getLibPath());
		GlobalClassLoader.addSystemClassPathOfFolder(serviceClassPath);
		//初始化日志配置文件
		PropertyConfigurator.configure(ServerBoot.class.getClassLoader().getResource("log4j.properties"));
		
		DynamicClassLoader classLoader = new DynamicClassLoader();
		classLoader.addFolder(GlobalParameter.getInstance().getLibPath());
		classLoader.addClassFolder(GlobalParameter.getInstance().getServiceClasses());
		
		ScanClass.scanClass(serviceFolderPath, classLoader);
		
		//启动socket服务以接收连接
		SocketServer socketServer = new SocketServer(Constants.servicePort);
		try {
			socketServer.start();
		} catch(Exception e) {
			socketServer.stop();
		}
		
		ZookeeperManager.getInstance().registServer();
	}

}
