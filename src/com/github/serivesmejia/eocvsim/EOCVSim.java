package com.github.serivesmejia.eocvsim;

import java.awt.Dimension;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.gui.Visualizer.AsyncPleaseWaitDialog;
import com.github.serivesmejia.eocvsim.input.ImageSource;
import com.github.serivesmejia.eocvsim.input.InputSourceManager;
import com.github.serivesmejia.eocvsim.pipeline.PipelineManager;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.SysUtil;

public class EOCVSim {

	public volatile Visualizer visualizer = new Visualizer(this);
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
		
		visualizer.init(); inputSourceManager.init();
		
		AsyncPleaseWaitDialog lookForPipelineAPWD = visualizer.asyncPleaseWaitDialog("Looking for pipelines...", "Scanning classpath", "Exit", new Dimension(300, 150), true);
		
		lookForPipelineAPWD.onCancel(new Runnable() {
			@Override
			public void run() {
				System.exit(0);
			}
		});
		
		pipelineManager.init(lookForPipelineAPWD);
		
		lookForPipelineAPWD.destroyDialog();
		
		//inputSourceManager.setInputSource(new ImageSource("ug_images/4.jpg", new Size(580, 380)));
		
		visualizer.updatePipelinesList();
		
		beginLoop();
		
	}
	
	public void beginLoop() {
		
		Log.info("EOCVSim", "Begin EOCVSim loop");
	
		while(!Thread.interrupted()) {
			
			//System.out.println(visualizer.frame.getSize());
			
			updateVisualizerTitle();
			
			if(inputSourceManager.lastMatFromSource == null || inputSourceManager.lastMatFromSource.empty()) continue;
			
			pipelineManager.update(inputSourceManager.lastMatFromSource);
			visualizer.updateVisualizedMat(pipelineManager.lastOutputMat);
			
			System.gc(); //run JVM garbage collector
			
		}
		
	}
	
	public void updateVisualizerTitle() {
		
		String fpsMsg = " (" + String.valueOf(pipelineManager.lastFPS) + " FPS)";
		
		String memoryMsg = " (" + String.valueOf(SysUtil.getMemoryUsageMB()) + " MB memory used)";
		
		if(pipelineManager.currentPipeline == null) {
			visualizer.setTitleMessage("No pipeline" + fpsMsg + memoryMsg);
		} else {
			visualizer.setTitleMessage(pipelineManager.currentPipelineName + fpsMsg + memoryMsg);
		}
		
	}

}
