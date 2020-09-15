package com.github.serivesmejia.eocvsim.pipeline;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.util.Log;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class PipelineManager {

	public ArrayList<Class<OpenCvPipeline>> pipelines = new ArrayList<Class<OpenCvPipeline>>();
	
	public OpenCvPipeline currentPipeline = null;
	public String currentPipelineName = "";

	public Mat lastOutputMat = new Mat();
	
	public int lastFPS = 0;
	
	private int fpsC = 0;
	private long nextFPSUpdateMillis = 0;
	
	public void init() {
		
		Log.info("PipelineManager", "Initializing...");
		Log.white();
		
		lookForPipelines();
		
		nextFPSUpdateMillis = System.currentTimeMillis();
		
	}
	
	@SuppressWarnings("unchecked")
	public void lookForPipelines() {
		
		Log.info("PipelineManager", "Scanning for pipelines...");
		
		ClassGraph classGraph = new ClassGraph()
									//.verbose()
									.enableAllInfo()
									.acceptPackages("org.firstinspires.ftc.teamcode");
		
		ScanResult scanResult = classGraph.scan();
		
	    for (ClassInfo routeClassInfo : scanResult.getAllClasses()) {
	    	
	    	boolean isPipelineAnnotated = false;
			
	    	for(AnnotationInfo annotInfo : routeClassInfo.getAnnotationInfo()) {
	    		
	    		Class<?> annotClass = null;
	    		
				try { 
					annotClass = Class.forName(annotInfo.getName()); 
				} catch (ClassNotFoundException e) { 
					e.printStackTrace(); 
				}
				
	    		if(annotClass == Pipeline.class) {
		    		isPipelineAnnotated = true;	
		    		break;
	    		}
	    		
	    	}
	    	
	    	Log.info("PipelineManager", "Found class " + routeClassInfo.getName() + " | isPipelineAnnotated: " + isPipelineAnnotated);
	    	
	    	if(isPipelineAnnotated) {
	    		
	    		Class<?> pipelineClass = null;
	    		
				try { 
					pipelineClass = Class.forName(routeClassInfo.getName()); 
				} catch (ClassNotFoundException e) { 
					e.printStackTrace(); 
				}

				try {
					pipelines.add((Class<OpenCvPipeline>) pipelineClass);
				} catch (Throwable ex) {
					ex.printStackTrace();
					Log.error("PipelineManager", "Unable to cast " + pipelineClass.getName() + " to OpenCvPipeline class.");
					Log.error("PipelineManager", "Remember that the pipeline class should extend OpenCvPipeline");
				}
					
	    	}
	    	
	    }
	    
	    Log.info("PipelineManager", "Found " + pipelines.size() + " pipeline(s)");
	    Log.white();
		
	    changePipeline(0);
	    
	}
	
	public void update(Mat inputMat) {
		
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
	
	public void changePipeline(int index) {
	
		Class<OpenCvPipeline> pipelineClass = pipelines.get(index);
	
		Log.info("PipelineManager", "Changing to pipeline " + pipelineClass.getName());
		
		Constructor<?> constructor;
		try {
			constructor = pipelineClass.getConstructor();
			currentPipeline = (OpenCvPipeline) constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("PipelineManager", "Unable to instantiate class " + pipelineClass.getName());
		}
		
		Log.info("PipelineManager", "Instantiated pipeline class " + pipelineClass.getName());
		
		currentPipeline.init(EOCVSim.EMPTY_MAT);
		
		Log.info("PipelineManager", "Initialized pipeline " + pipelineClass.getName());
		Log.white();
		
		currentPipelineName = currentPipeline.getClass().getSimpleName();
		
	}
	
}
