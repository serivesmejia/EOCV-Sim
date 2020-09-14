package com.github.serivesmejia.eocvsim.util;

public class SysUtil {

	public enum OperatingSystem {
		WINDOWS,
		LINUX,
		MACOS,
		UNKNOWN
	}
	
	public static OperatingSystem getOS() {
		
		String osName = System.getProperty("os.name").toLowerCase();
		
		if(osName.contains("win")) {
			return OperatingSystem.WINDOWS;
		} else if(osName.contains("nux")) {
			return OperatingSystem.LINUX;
		}
		
		return OperatingSystem.UNKNOWN;
		
	}
	
}
