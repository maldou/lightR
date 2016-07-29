package com.maldou.lightr.server.classutil;

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;

import com.maldou.lightr.server.invoker.ProxyFactory;
import com.maldou.lightr.server.util.ClassHelper;
import com.maldou.lightr.server.util.GlobalParameter;

public class ScanClass {
	
	private static Logger logger = Logger.getLogger(ScanClass.class);
	
	public static void scanClass(String root, ClassLoader classLoader) throws Exception {
		doScanClass(root, classLoader);
	}
	
	private static void doScanClass(String currentRoot, ClassLoader classLoader) throws Exception{
		File rootFile = new File(currentRoot);
		if(rootFile.isDirectory()) {
			File[] children = rootFile.listFiles();
			for(File currentFile : children) {
				if(currentFile.isDirectory()) {
					doScanClass(currentFile.getAbsolutePath(), classLoader);
				}
				else if(currentFile.isFile()) {
					String path = currentFile.getCanonicalPath();
					if(path.endsWith(".class")) {
						String baseClassPath = GlobalParameter.getInstance().getServiceName() + "/classes/";
						int length = baseClassPath.length();
						int index = path.lastIndexOf(baseClassPath);
						if(index > 0) {
							String className = path.substring(index + length).replaceAll(".class", "").replaceAll("/", ".");
							logger.info("classname:" + className);
							ProxyFactory.createProxy(className, classLoader);
						}
					}
					else if(path.endsWith(".jar")) {
						logger.info("扫描jar:" + path);
						Set<String> classNames = ClassHelper.getClassNamesFromJar(path);
						for(String className : classNames) {
							logger.info("classname:" + className);
							ProxyFactory.createProxy(className, classLoader);
						}
					}
				}
				
			}
		}
	}
}
