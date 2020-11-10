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

public class PipelineManager {

	public volatile ArrayList<Class<OpenCvPipeline>> pipelines = new ArrayList<>();
	
	public OpenCvPipeline currentPipeline = null;
	public String currentPipelineName = "";
	public int currentPipelineIndex = -1;

	public Telemetry currentTelemetry = null;

	public volatile Mat lastOutputMat = new Mat();

	public int lastFPS = 0;
	private int fpsCount = 0;
	private long nextFPSUpdateMillis = 0;

	private volatile boolean isPaused = false;
	private volatile PauseReason lastPauseReason = PauseReason.NOT_PAUSED;

	private final ArrayList<Runnable> runnsOnUpdate = new ArrayList<>();
	private final ArrayList<Runnable> runnsOnPause = new ArrayList<>();
	private final ArrayList<Runnable> runnsOnResume = new ArrayList<>();

	public EOCVSim eocvSim;

	public enum PauseReason { USER_REQUESTED, IMAGE_ONE_ANALYSIS, NOT_PAUSED }

	public PipelineManager(EOCVSim eocvSim) {
		this.eocvSim = eocvSim;
	}

	public void init(AsyncPleaseWaitDialog lookForPipelineAPWD) {
		
		Log.info("PipelineManager", "Initializing...");

		//add default pipeline
		addPipelineClass(DefaultPipeline.class);

		//scan for pipelines
		new PipelineScanner(this).lookForPipelines(lookForPipelineAPWD);

		Log.info("PipelineManager", "Found " + pipelines.size() + " pipeline(s)");
		Log.white();

		nextFPSUpdateMillis = System.currentTimeMillis(); //the next time the FPS counter will be updated, in milliseconds
		requestChangePipeline(0); //change to the default pipeline

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
		for(Runnable runn : runnsOnUpdate.toArray(new Runnable[0])) {
			runn.run();
			runnsOnUpdate.remove(runn);
		}

		if(isPaused) {
			if(lastOutputMat == null || lastOutputMat.empty())
				lastOutputMat = inputMat;
			return;
		}

		if(currentPipeline != null) {
			lastOutputMat = currentPipeline.processFrame(inputMat);
		} else {
			lastOutputMat = inputMat;
		}

		calcFPS();

	}
	
	private void calcFPS() {
		
		fpsCount++;

		//update and reset the fps count if a second has passed since the last update
		if(System.currentTimeMillis() >= nextFPSUpdateMillis) {
			nextFPSUpdateMillis = System.currentTimeMillis() + 1000;
			lastFPS = fpsCount;
			fpsCount = 0;
		}
		
	}
	
	public void changePipeline(int index) {

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

		eocvSim.inputSourceManager.pauseIfImageTwoFrames(); //pause next frame if current selected inputsource is an image

	}

	public void requestChangePipeline(int index) {
		runOnUpdate(new Runnable() {
			@Override
			public void run() {
				changePipeline(index);
			}
		});
	}

	public void runThenPause() {

		setPaused(false);

		eocvSim.runOnMainThread(new Runnable() {
			@Override
			public void run() {
				setPaused(true);
			}
		});

	}

	public void runOnPause(Runnable runn) {
		runnsOnPause.add(runn);
	}

	public void runOnResume(Runnable runn) {
		runnsOnResume.add(runn);
	}

	public void runOnUpdate(Runnable runn) {
		runnsOnUpdate.add(runn);
	}

	public void setPaused(boolean paused) {
		setPaused(paused, PauseReason.USER_REQUESTED);
	}

	public void setPaused(boolean paused, PauseReason pauseReason) {

		isPaused = paused;

		if(isPaused) {
			lastPauseReason = pauseReason;
		} else {
			lastPauseReason = PauseReason.NOT_PAUSED;
		}

		eocvSim.visualizer.pipelinePauseBtt.setSelected(isPaused);
		executeRunnsOnPauseOrResume();

	}

	public void togglePause() {
		setPaused(!isPaused);
		executeRunnsOnPauseOrResume();
	}

	public void requestSetPaused(boolean paused, PauseReason pauseReason) {
		eocvSim.runOnMainThread(new Runnable() {
			@Override
			public void run() {
				setPaused(paused, pauseReason);
			}
		});
	}

	public void requestSetPaused(boolean paused) {
		requestSetPaused(paused, PauseReason.USER_REQUESTED);
	}

	public boolean isPaused() {
		if(!isPaused) lastPauseReason = PauseReason.NOT_PAUSED;
		return isPaused;
	}

	public PauseReason getPauseReason() {
		if(!isPaused) lastPauseReason = PauseReason.NOT_PAUSED;
		return lastPauseReason;
	}

	private void executeRunnsOnPauseOrResume() {
		if(isPaused) {
			executeRunnsOnPause();
		} else {
			executeRunnsOnResume();
		}
	}

	private void executeRunnsOnPause() {
		//run all pending requested runnables
		for(Runnable runn : runnsOnPause.toArray(new Runnable[0])) {
			runn.run();
		}
	}

	private void executeRunnsOnResume() {
		//run all pending requested runnables
		for(Runnable runn : runnsOnResume.toArray(new Runnable[0])) {
			runn.run();
		}
	}

}
