package com.maldou.lightr.server.classutil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.maldou.lightr.server.util.FileHelper;

import sun.misc.Launcher;

@SuppressWarnings("restriction")
public class GlobalClassLoader {
	private static Method addURL;

	static {
		try {
			addURL = URLClassLoader.class.getDeclaredMethod("addURL",
					new Class[] { URL.class });
		} catch (Exception e) {
			e.printStackTrace();
		}
		addURL.setAccessible(true);
	}

	private static URLClassLoader system = (URLClassLoader) getSystemClassLoader();

	private static URLClassLoader ext = (URLClassLoader) getExtClassLoader();

	public static ClassLoader getSystemClassLoader() {
		return ClassLoader.getSystemClassLoader();
	}

	public static ClassLoader getExtClassLoader() {
		return getSystemClassLoader().getParent();
	}

	public static void addURL2SystemClassLoader(URL url) throws Exception {
		try {
			addURL.invoke(system, new Object[] { url });
		} catch (Exception e) {
			throw e;
		}
	}

	public static void addURL2ExtClassLoader(URL url) throws Exception {
		try {
			addURL.invoke(ext, new Object[] { url });
		} catch (Exception e) {
			throw e;
		}
	}

	public static void addSystemClassPath(String path) throws Exception {
		try {
			URL url = new URL("file", "", path);
			addURL2SystemClassLoader(url);
		} catch (MalformedURLException e) {
			throw e;
		}
	}
	
	/**
	 * 加载目录下的资源配置文件（xml文件，properties文件，config文件等）到classpath(只需加载到目录层即可)
	 * @param dirs
	 */
	public static void addSystemClassPathOfFolder(String[] dirs) {
		for(String dir : dirs) {
			File file = new File(dir);
			if(file.isDirectory()) {
				try {
					addURL2SystemClassLoader(file.toURI().toURL());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void addExtClassPath(String path) throws Exception {
		try {
			URL url = new URL("file", "", path);
			addURL2ExtClassLoader(url);
		} catch (MalformedURLException e) {
			throw e;
		}
	}

	public static void addSystemClassPathOfJar(String... dirs)
			throws Exception {
		List<String> jarList = FileHelper.getUniqueLibPath(dirs);
		for (String jar : jarList) {
			addSystemClassPath(jar);
		}
	}

	public static void addURL2ExtClassLoaderFolder(String... dirs)
			throws Exception {
		List<String> jarList = FileHelper.getUniqueLibPath(dirs);
		for (String jar : jarList) {
			addExtClassPath(jar);
		}
	}

	public static URL[] getBootstrapURLs() {
		return Launcher.getBootstrapClassPath().getURLs();
	}

	public static URL[] getSystemURLs() {
		return system.getURLs();
	}

	public static URL[] getExtURLs() {
		return ext.getURLs();
	}
}
