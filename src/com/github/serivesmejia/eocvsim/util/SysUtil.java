package com.github.serivesmejia.eocvsim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.opencv.core.Core;

public class SysUtil {

	public static OperatingSystem OS = SysUtil.getOS();
	
	public enum OperatingSystem {
		WINDOWS,
		LINUX,
		OSX,
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
		String fileExt = null;
		
		switch(OS) { //getting os prefix
			case WINDOWS:
				os = "win";
				fileExt = "dll";
				break;
			default:
				os = "nux";
				fileExt = "so";
				break;
		}
		
		boolean is64bit = System.getProperty("sun.arch.data.model").contains("64"); //Checking if JVM is 64 bits or not
		
		try {
			loadLib(os, fileExt, is64bit, Core.NATIVE_LIBRARY_NAME, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
	}
	
	public static void loadLib(String os, String fileExt, boolean is64bit, String name, int attemps) throws IOException {
	
		String arch = is64bit ? "64" : "32"; //getting os arch	
		
		String libName = os + arch + "_" + name; //resultant lib name from those two
		String libNameExt = libName + "." + fileExt; //resultant lib name from those two
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		
		InputStream libIs = SysUtil.class.getResourceAsStream("/" + libNameExt);
		File tempLibFile = new File(tmpDir + libNameExt);
		
		Log.info("SysUtil", "Copying native lib \"" + libNameExt + "\" to \"" + tmpDir+ "\"");
		
		try {
			if(!copyFileIs(libIs, tempLibFile, false).alreadyExists) {
				Log.info("SysUtil", "Copy of " + libName + " cancelled since file already exists");
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		
		Log.white();
		
		Log.info("SysUtil", "Loading native lib \"" + libNameExt + "\"");
		
		try {
			
			System.load(tempLibFile.getAbsolutePath()); //Loading OpenCV native library
			Log.info("SysUtil", "Successfully loaded native lib \"" + libName + "\"");
			
		} catch (UnsatisfiedLinkError ex) {
			
			ex.printStackTrace();
			
			if(attemps < 4) {
				ex.printStackTrace();
				Log.error("SysUtil", "Failure loading lib \"" + libName + "\", retrying with different architecture... (" + attemps + " attemps)");
				loadLib(os, fileExt, !is64bit, Core.NATIVE_LIBRARY_NAME, attemps + 1);
			} else {
				ex.printStackTrace();
				Log.error("SysUtil", "Failure loading lib \"" + libName + "\" 4 times, the application will exit now.");
				System.exit(1);
			}
			
		}
		
	}
	
	public static CopyFileIsData copyFileIs(InputStream is, File toPath, boolean replaceIfExisting) throws IOException {
		
		boolean alreadyExists = true;
		
		if(toPath.exists()) {
			if(replaceIfExisting) {
				Files.copy(is, toPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				alreadyExists = false;
			}
		} else {
			Files.copy(is, toPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		
		is.close();
		
		CopyFileIsData data = new CopyFileIsData();
		data.alreadyExists = alreadyExists;
		data.file = toPath;
		
		return data;
		
	}
	
	public static CopyFileIsData copyFileIsTemp(InputStream is, String fileName, boolean replaceIfExisting) throws IOException {
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		
		File tempFile = new File(tmpDir + fileName);
		
		return copyFileIs(is, tempFile, replaceIfExisting);
		
	}

	public static long getMemoryUsageMB() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB;
	}
	
	public static class CopyFileIsData {
		
		public File file = null;
		public boolean alreadyExists = false;	

	}
	
}
