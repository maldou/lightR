package com.maldou.lightr.server.classutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.maldou.lightr.server.util.FileHelper;

public class DynamicClassLoader extends SecureClassLoader {
	/**
	 * jar list load class from this
	 */
	private static List<String> jarList = new ArrayList<String>();
	
	private static String classFolder = "";
	
	/**
	 * class cache
	 */
	private Map<String,Class<?>> classCache = new HashMap<String,Class<?>>();
	
	
	public DynamicClassLoader(ClassLoader parent){
		super(parent);
	}
	
	public DynamicClassLoader() {
		
	}
	
	/**
	 * dynamic find class from jar
	 * @param jarPath
	 * @param className
	 * @param fromCache
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Class<?> findClass(String jarPath, String className, boolean fromCache) throws ClassNotFoundException {		
		if(fromCache && classCache.containsKey(className)) {
			return classCache.get(className);
		}
		
		String classPath = className.replace('.', '/').concat(".class");
		byte[] clsByte = null;
		boolean isClassFolderFind = false;
		if(jarPath==null || jarPath.equalsIgnoreCase("")) {
			if(classFolder != null && classFolder.length() > 0) {
				File classFile = new File(classFolder + classPath);
				try {
					InputStream input = new FileInputStream(classFile);
					clsByte = new byte[input.available()];
					input.read(clsByte);
					input.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
			if(clsByte != null && clsByte.length > 0) {
				isClassFolderFind = true;
			}
			else {
				for(String jp : jarList) {
					jarPath = jp;
					clsByte = getClassByte(jp, classPath);
					if(clsByte != null) {
						break;
					}
				}
//				clsByte = getClassByte(classPath);
			}
		} else {
			clsByte = getClassByte(jarPath, classPath);
		}
		
		if(clsByte == null) {
			throw new ClassNotFoundException(className);
		}
		
		URL url = null;
		try {
			url = new URL("file", "", isClassFolderFind? classFolder : jarPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return findClass(className, clsByte, url);
	}
	
	
	/**
	 * 
	 * @param className
	 * @param clsByte
	 * @return
	 */
	public Class<?> findClass(String className, byte[] clsByte, URL url) {
		Class<?> cls = null;
		try {
			CodeSource cs = new CodeSource(url, (java.security.cert.Certificate[]) null);
			ProtectionDomain pd = new ProtectionDomain(cs, null, this, null);
			cls = super.defineClass(className, clsByte, 0, clsByte.length, pd);
			resolveClass(cls);
			classCache.put(className, cls);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return cls;
	}
	
	/**
	 * dynamic find class from jar
	 * @param jarPath
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Class<?> findClass(String jarPath, String className) throws ClassNotFoundException {
		return findClass(jarPath, className, true);
	}
	
	/**
	 * dynamic find class from jar
	 */
	@Override
	public Class<?> findClass(String className) throws ClassNotFoundException {
		return findClass("", className, true);
	}
	
	/**
	 * dynamic find class from jar
	 * @param className
	 * @param fromCache
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Class<?> findClass(String className, boolean fromCache) throws ClassNotFoundException {
		return findClass("", className, fromCache);
	}
	
	/**
	 * clear all class cache
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void clearAllClassCache(){
		try {
			Iterator it = classCache.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Class<?>> entry = (Map.Entry<String, Class<?>>) it.next();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		classCache.clear();
	}
	
	/**
	 * add jar url
	 * @param url
	 * @throws Exception
	 */
	public void addURL(String url) {
		if(!jarList.contains(url)){
			jarList.add(url);
//			try {
//				ucp.addURL(new URL("file", "", url));
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	/**
	 * add folder jars
	 * @param path
	 * @throws IOException 
	 */
	public void addFolder(String... dirs) throws IOException {
		List<String> jarList = FileHelper.getUniqueLibPath(dirs);
		for(String jar : jarList) {
			addURL(jar);
		}
	}
	
	/**
	 * classfolder  bin��Ŀ¼
	 * @param dir
	 */
	public void addClassFolder(String dir) {
		classFolder = dir;
	}
	
	/**
	 * get jar list
	 * @return
	 */
	public List<String> getJarList(){
		return jarList;
	}
	
//	/**
//	 * get class byte[] from jarList
//	 * @param classPath
//	 * @return
//	 */
//	private byte[] getClassByte(String classPath) {
//		byte[] clsByte = null;
//		for(String jarPath : jarList) {
//			clsByte = getClassByte(jarPath, classPath);
//			if(clsByte != null) {
//				break;
//			}
//		}
//		return clsByte;
//	}
//	
	/**
	 * get class byte from jarPath
	 * @param jarPath
	 * @param classPath
	 * @return
	 */
	private byte[] getClassByte(String jarPath, String classPath) {
		JarFile jarFile = null;
		InputStream input = null;
		byte[] clsByte = null;
		try {
			jarFile = new JarFile(jarPath);  // read jar
			JarEntry entry = jarFile.getJarEntry(classPath); // read class file
			if(entry != null) {
				input = jarFile.getInputStream(entry); 
				clsByte = new byte[input.available()];
				input.read(clsByte);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(jarFile != null) {
				try {
					jarFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return clsByte;
	}
}