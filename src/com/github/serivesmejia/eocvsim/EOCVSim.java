package com.github.serivesmejia.eocvsim;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.input.ImageSource;
import com.github.serivesmejia.eocvsim.input.InputSourceManager;
import com.github.serivesmejia.eocvsim.pipeline.PipelineManager;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.SysUtil;

public class EOCVSim {

	public Visualizer visualizer = new Visualizer(this);
	public PipelineManager pipelineManager = null;
	
	public InputSourceManager inputSourceManager = new InputSourceManager();
	
	public static Mat EMPTY_MAT = null;
	public static String VERSION = "1.0.0-alpha";

	public void init() {

		Log.info("EOCVSim", "Initializing EOCV Sim v" + VERSION);
		Log.white();
		
		SysUtil.loadCvNativeLib();
		Log.white();
		
		EMPTY_MAT = new Mat();
		
		pipelineManager = new PipelineManager();
		
		visualizer.init();
		pipelineManager.init();
		inputSourceManager.init();
		
		inputSourceManager.setInputSource(new ImageSource("src/test_imgs/4.jpg", new Size(540, 380)));
		
		beginLoop();
		
	}
	
	public void beginLoop() {
		
		Log.info("EOCVSim", "Begin EOCVSim loop");
	
		while(!Thread.interrupted()) {
			
			String fpsMsg = " (" + String.valueOf(pipelineManager.lastFPS) + " FPS)";
			
			String memoryMsg = " (" + String.valueOf(SysUtil.getMemoryUsageMB()) + " MB memory used)";
			
			if(pipelineManager.currentPipeline == null) {
				visualizer.setTitleMessage("No pipeline" + fpsMsg + memoryMsg);
			} else {
				visualizer.setTitleMessage(pipelineManager.currentPipelineName + fpsMsg + memoryMsg);
			}
			
			if(inputSourceManager.lastMatFromSource == null || inputSourceManager.lastMatFromSource.empty()) continue;
			
			pipelineManager.update(inputSourceManager.lastMatFromSource);
			visualizer.updateVisualizedMat(pipelineManager.lastOutputMat);
			
			System.gc(); //run jvm garbage collector
			
		}
		
	}

}
