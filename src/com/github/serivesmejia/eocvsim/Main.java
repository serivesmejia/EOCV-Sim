package com.github.serivesmejia.eocvsim;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import com.github.serivesmejia.eocvsim.gui.Visualizer;

public class Main {

	public static Visualizer visualizer = new Visualizer();
	
	public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //Loading OpenCV native library
        
		visualizer.init();
		
	}

}
