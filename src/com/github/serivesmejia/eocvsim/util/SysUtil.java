package com.github.serivesmejia.eocvsim.util;

import org.opencv.core.Core;

public class SysUtil {

	public static OperatingSystem OS = SysUtil.getOS();
	
	public enum OperatingSystem {
		WINDOWS,
		LINUX,
		MACOS,
		UNKNOWN
	}
	
	public static int MB = 1024 * 1024;
	
	public static OperatingSystem getOS() {
		
		String osName = System.getProperty("os.name").toLowerCase();
		
		if(osName.contains("win")) {
			return OperatingSystem.WINDOWS;
		} else if(osName.contains("nux")) {
			return OperatingSystem.LINUX;
		}
		
		return OperatingSystem.UNKNOWN;
		
	}
	
	public static void loadCvNativeLib() {
		
		String os = null;
		
		switch(OS) { //getting os prefix
			case WINDOWS:
				os = "win";
				break;
			default:
				os = "nux";
				break;
		}
		
		boolean is64bit = System.getProperty("sun.arch.data.model").contains("64");
		
		loadLib(os, is64bit, Core.NATIVE_LIBRARY_NAME, 0);
		
	}
	
	public static void loadLib(String os, boolean is64bit, String name, int attemps) {
	
		String arch = is64bit ? "64" : "32"; //getting os arch	
		
		String libName = os + arch + "_" + name; //resultant lib name from those two
		
		Log.info("SysUtil", "Loading native lib \"" + libName + "\"");
		
		try {
			
			System.loadLibrary(libName); //Loading OpenCV native library
			Log.info("SysUtil", "Successfully loaded native lib \"" + libName + "\"");
			
		} catch (UnsatisfiedLinkError ex) {
			
			ex.printStackTrace();
			
			if(attemps < 4) {
				Log.error("SysUtil", "Failure loading lib \"" + libName + "\", retrying with different architecture... (" + attemps + " attemps)");
				loadLib(os, !is64bit, Core.NATIVE_LIBRARY_NAME, attemps + 1);
			} else {
				Log.error("SysUtil", "Failure loading lib \"" + libName + "\" 4 times, the application will exit now.");
				System.exit(1);
			}
			
		}
		
	}

	public static long getMemoryUsageMB() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB;
	}
	
}
