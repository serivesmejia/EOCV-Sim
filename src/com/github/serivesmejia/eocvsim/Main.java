package com.github.serivesmejia.eocvsim;

import org.opencv.core.Core;

import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.util.SysUtil;
import com.github.serivesmejia.eocvsim.util.SysUtil.OperatingSystem;

public class Main {

	public static Visualizer visualizer = new Visualizer();
	public static PipelineManager pipelineManager = new PipelineManager();
	
	public static OperatingSystem OS = SysUtil.getOS();
	
	public static void main(String[] args) {

		loadCvNativeLib();
		
		visualizer.init();
		
	}
	
	private static void loadCvNativeLib() {
		
		String os = null;
		
		boolean is64bit = System.getProperty("sun.arch.data.model").contains("64");
		
		switch(OS) { //getting os prefix
			case WINDOWS:
				os = "win";
				break;
			default:
				os = "nux";
				break;
		}
		
		String arch = is64bit ? "64" : "32"; //getting os arch
		
		String libName = os + arch + "_" + Core.NATIVE_LIBRARY_NAME; //resultant lib name from those two
		
		System.out.println("Loading native lib \"" + libName + "\"");
		
		try {
			System.loadLibrary(libName); //Loading OpenCV native library
			System.out.println("Successfully loaded native lib \"" + libName + "\"");
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		
	}

}
