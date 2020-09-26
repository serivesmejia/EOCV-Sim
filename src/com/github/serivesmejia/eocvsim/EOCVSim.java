package com.github.serivesmejia.eocvsim;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.github.serivesmejia.eocvsim.pipeline.DefaultPipeline;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.gui.Visualizer.AsyncPleaseWaitDialog;
import com.github.serivesmejia.eocvsim.input.ImageSource;
import com.github.serivesmejia.eocvsim.input.InputSourceManager;
import com.github.serivesmejia.eocvsim.pipeline.PipelineManager;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.SysUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class EOCVSim {

	public volatile Visualizer visualizer = new Visualizer(this);
	public PipelineManager pipelineManager = null;
	
	public InputSourceManager inputSourceManager = new InputSourceManager(this);

	private String beforeSelectedSource = "";
	private int beforeSelectedPipeline = -1;

	public static Mat EMPTY_MAT = null;
	public static String VERSION = "1.0.0";

	private volatile ArrayList<Runnable> runnsOnMain = new ArrayList<>();

	public void init() {

		Log.info("EOCVSim", "Initializing EOCV Sim v" + VERSION);
		Log.white();
		
		SysUtil.loadCvNativeLib();
		Log.white();
		
		EMPTY_MAT = new Mat();
		
		pipelineManager = new PipelineManager();
		
		visualizer.init();

		setVisualizerEvts();

		inputSourceManager.init();

		visualizer.updateSourcesList();
		visualizer.sourceSelector.setSelectedIndex(0);

		AsyncPleaseWaitDialog lookForPipelineAPWD = visualizer.asyncPleaseWaitDialog("Looking for pipelines...", "Scanning classpath", "Exit", new Dimension(300, 150), true);
		
		lookForPipelineAPWD.onCancel(new Runnable() {
			@Override
			public void run() {
				System.exit(0);
			}
		});
		
		pipelineManager.init(lookForPipelineAPWD);

		lookForPipelineAPWD.destroyDialog();

		visualizer.updatePipelinesList();
		visualizer.pipelineSelector.setSelectedIndex(0);
		
		beginLoop();
		
	}
	
	public void beginLoop() {
		
		Log.info("EOCVSim", "Begin EOCVSim loop");
		Log.white();

		inputSourceManager.inputSourceLoader.saveInputSourcesToFile();

		while(!Thread.interrupted()) {

			for(Object runn : runnsOnMain.toArray()) {
				((Runnable) runn).run();
			}

			runnsOnMain.clear();

			updateVisualizerTitle();

			inputSourceManager.update();

			if(inputSourceManager.lastMatFromSource == null || inputSourceManager.lastMatFromSource.empty()) continue;

			try {
				pipelineManager.update(inputSourceManager.lastMatFromSource);
				visualizer.updateVisualizedMat(pipelineManager.lastOutputMat);
			} catch(Throwable ex) { Log.error("Error while processing pipeline", ex); }
			
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

	public void setVisualizerEvts() {

		visualizer.pipelineSelector.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if(visualizer.pipelineSelector.getSelectedIndex() != -1) {

					int pipeline = visualizer.pipelineSelector.getSelectedIndex();
					if (!evt.getValueIsAdjusting() && pipeline != beforeSelectedPipeline) {
						pipelineManager.changePipelineNextFrame(pipeline);
						beforeSelectedPipeline = pipeline;
					}

				} else {
					visualizer.pipelineSelector.setSelectedIndex(1);
				}
			}

		});

		visualizer.sourceSelector.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {

				try {
					if(visualizer.sourceSelector.getSelectedIndex() != -1) {

						ListModel<String> model = visualizer.sourceSelector.getModel();
						String source = model.getElementAt(visualizer.sourceSelector.getSelectedIndex());

						if (!evt.getValueIsAdjusting() && source != beforeSelectedSource) {
							inputSourceManager.setInputSourceNextFrame(source);
							beforeSelectedSource = source;
						}

					} else {
						visualizer.sourceSelector.setSelectedIndex(1);
					}
				} catch(ArrayIndexOutOfBoundsException ex) { }

			}

		});

		//handling onViewportTapped evts
		visualizer.img.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				pipelineManager.currentPipeline.onViewportTapped();
			}

		});

	}

	public void runOnMainThread(Runnable runn) {
		runnsOnMain.add(runn);
	}

}
