package com.github.serivesmejia.eocvsim.pipeline;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.Visualizer.AsyncPleaseWaitDialog;
import com.github.serivesmejia.eocvsim.util.Log;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class PipelineManager {

	public volatile ArrayList<Class<OpenCvPipeline>> pipelines = new ArrayList<>();
	
	public OpenCvPipeline currentPipeline = null;
	public String currentPipelineName = "";
	public int currentPipelineIndex = -1;

	public Telemetry currentTelemetry = null;

	public volatile Mat lastOutputMat = new Mat();

	public int lastFPS = 0;
	private int fpsC = 0;
	private long nextFPSUpdateMillis = 0;

	private final ArrayList<Runnable> runnsOnUpdate = new ArrayList<>();

	public EOCVSim eocvSim;

	public PipelineManager(EOCVSim eocvSim) {
		this.eocvSim = eocvSim;
	}

	public void init(AsyncPleaseWaitDialog lookForPipelineAPWD) {
		
		Log.info("PipelineManager", "Initializing...");

		lookForPipelines(lookForPipelineAPWD);
		
		nextFPSUpdateMillis = System.currentTimeMillis();

		requestChangePipeline(0);

	}
	
	@SuppressWarnings("unchecked")
	public void lookForPipelines(AsyncPleaseWaitDialog lookForPipelineAPWD) {
		
		Log.info("PipelineManager", "Scanning for pipelines...");
		
		//Scan for all classes in the org.firstinspires package
		ClassGraph classGraph = new ClassGraph().enableAllInfo().acceptPackages("org.firstinspires");
		
		ScanResult scanResult = classGraph.scan();

		addPipelineClass(DefaultPipeline.class);

		//iterate over the results of the scan
	    for (ClassInfo routeClassInfo : scanResult.getAllClasses()) {
	    	
	    	Class<?> foundClass = null;
	    	
			try {
				foundClass = Class.forName(routeClassInfo.getName());
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				continue; //continue because we couldn't get the class...
			}

			//Scan recursively until we find a OpenCvPipeline superclass or we hit the Object superclass
			Class<?> superClass = foundClass.getSuperclass();
			while (superClass != null) {
				
				if(superClass == OpenCvPipeline.class){ //Yay we found a pipeline

					Log.info("PipelineManager", "Found pipeline " + routeClassInfo.getName());
					if(lookForPipelineAPWD != null) lookForPipelineAPWD.subMsg.setText("Found pipeline " + routeClassInfo.getSimpleName()); 
					
					addPipelineClass(foundClass);
					break;
					
				}
				
				//Didn't found a pipeline, continue searching...
				superClass = superClass.getSuperclass();
				
			}
		
	    }
	    
	    Log.info("PipelineManager", "Found " + pipelines.size() + " pipeline(s)");
	    Log.white();

	}
	
	public void addPipelineClass(Class<?> C) {
		try {
			pipelines.add((Class<OpenCvPipeline>) C);
		} catch (Throwable ex) {
			ex.printStackTrace();
			Log.error("PipelineManager", "Unable to cast " + C.getName() + " to OpenCvPipeline class.");
			Log.error("PipelineManager", "Remember that the pipeline class should extend OpenCvPipeline");
		}
	}
	
	public void update(Mat inputMat) {

		//run all pending requested runnables
		for(Object runn : runnsOnUpdate.toArray()) {
			((Runnable) runn).run();
			runnsOnUpdate.remove(runn);
		}

		if(currentPipeline != null) {
			lastOutputMat = currentPipeline.processFrame(inputMat);
		} else {
			lastOutputMat = inputMat;
		}

		calcFPS();

	}
	
	private void calcFPS() {
		
		fpsC++;
		
		if(System.currentTimeMillis() >= nextFPSUpdateMillis) {
			
			nextFPSUpdateMillis = System.currentTimeMillis() + 1000;
			
			lastFPS = fpsC;
			fpsC = 0;
		
		}
		
	}
	
	public void setPipeline(int index) {

		if(index == currentPipelineIndex) return;

		OpenCvPipeline nextPipeline = null;
		Telemetry nextTelemetry;
		
		Class<OpenCvPipeline> pipelineClass = pipelines.get(index);
	
		Log.info("PipelineManager", "Changing to pipeline " + pipelineClass.getName());
		
		Constructor<?> constructor;
		try {

			constructor = pipelineClass.getConstructor();
			nextPipeline = (OpenCvPipeline) constructor.newInstance();

			nextTelemetry = new Telemetry();
			nextPipeline.telemetry = nextTelemetry;

			Log.info("PipelineManager", "Instantiated pipeline class " + pipelineClass.getName());

			nextPipeline.init(eocvSim.inputSourceManager.lastMatFromSource);

		} catch(Throwable ex) {

			eocvSim.visualizer.asyncPleaseWaitDialog("Error while initializing requested pipeline", "Falling back to previous one",
													 "Close", new Dimension(300, 150), true, true);

			Log.error("InputSourceManager", "Error while initializing requested pipeline ("+ pipelineClass.getSimpleName() + ")", ex);

			Log.white();

			eocvSim.visualizer.pipelineSelector.setSelectedIndex(currentPipelineIndex);

			return;

		}

		Log.info("PipelineManager", "Initialized pipeline " + pipelineClass.getName());
		Log.white();

		currentPipeline = nextPipeline;
		currentTelemetry = nextTelemetry;

		currentPipelineIndex = index;

		currentPipelineName = currentPipeline.getClass().getSimpleName();

	}

	public void requestChangePipeline(int index) {
		runOnUpdate(new Runnable() {
			@Override
			public void run() {
				setPipeline(index);
			}
		});
	}

	public void runOnUpdate(Runnable runn) {
		runnsOnUpdate.add(runn);
	}

}
