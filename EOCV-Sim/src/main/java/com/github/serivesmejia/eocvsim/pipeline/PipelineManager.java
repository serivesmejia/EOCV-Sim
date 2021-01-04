package com.github.serivesmejia.eocvsim.pipeline;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.event.EventHandler;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class PipelineManager {

    public final EventHandler onUpdate = new EventHandler("OnUpdate-PipelineManager");
    public final EventHandler onPipelineChange = new EventHandler("OnPipelineChange-PipelineManager");

    public final EventHandler onPause = new EventHandler("OnPause-PipelineManager");
    public final EventHandler onResume = new EventHandler("OnResume-PipelineManager");

    public volatile ArrayList<Class<? extends OpenCvPipeline>> pipelines = new ArrayList<>();

    public OpenCvPipeline currentPipeline = null;
    public String currentPipelineName = "";
    public int currentPipelineIndex = -1;

    public Telemetry currentTelemetry = null;
    public volatile Mat lastOutputMat = new Mat();

    public EOCVSim eocvSim;

    private volatile boolean isPaused = false;
    private volatile PauseReason lastPauseReason = PauseReason.NOT_PAUSED;

    public PipelineManager(EOCVSim eocvSim) {
        this.eocvSim = eocvSim;
    }

    public void init() {

        Log.info("PipelineManager", "Initializing...");

        //add default pipeline
        addPipelineClass(DefaultPipeline.class);

        //scan for pipelines
        new PipelineScanner().lookForPipelines(this::addPipelineClass);

        Log.info("PipelineManager", "Found " + pipelines.size() + " pipeline(s)");
        Log.white();

        requestChangePipeline(0); //change to the default pipeline

    }

    public void addPipelineClass(Class<?> C) {
        try {
            pipelines.add((Class<? extends OpenCvPipeline>) C);
        } catch (Throwable ex) {
            ex.printStackTrace();
            Log.error("PipelineManager", "Unable to cast " + C.getName() + " to OpenCvPipeline class.");
            Log.error("PipelineManager", "Remember that the pipeline class should extend OpenCvPipeline");
        }
    }

    public void update(Mat inputMat) {

        onUpdate.run();

        if (isPaused) {
            if (lastOutputMat == null || lastOutputMat.empty())
                lastOutputMat = inputMat;
            return;
        }

        if (currentPipeline != null) {
            lastOutputMat = currentPipeline.processFrame(inputMat);
        } else {
            lastOutputMat = inputMat;
        }

    }

    public void changePipeline(int index) {

        if (index == currentPipelineIndex) return;

        OpenCvPipeline nextPipeline = null;
        Telemetry nextTelemetry = null;

        Class<? extends OpenCvPipeline> pipelineClass = pipelines.get(index);

        Log.info("PipelineManager", "Changing to pipeline " + pipelineClass.getName());

        Constructor<?> constructor;
        try {

            nextTelemetry = new Telemetry();

            try { //instantiate pipeline if it has a constructor with a telemetry parameter
                constructor = pipelineClass.getConstructor(Telemetry.class);
                nextPipeline = (OpenCvPipeline) constructor.newInstance(nextTelemetry);
            } catch (NoSuchMethodException ex) { //instantiating with a constructor with no params
                constructor = pipelineClass.getConstructor();
                nextPipeline = (OpenCvPipeline) constructor.newInstance();
            }

            Log.info("PipelineManager", "Instantiated pipeline class " + pipelineClass.getName());

            nextPipeline.init(eocvSim.inputSourceManager.lastMatFromSource);

        } catch (NoSuchMethodException ex) {

            eocvSim.visualizer.asyncPleaseWaitDialog("Error while initializing requested pipeline", "Check console for details",
                    "Close", new Dimension(300, 150), true, true);

            Log.error("PipelineManager", "Error while initializing requested pipeline (" + pipelineClass.getSimpleName() + ")", ex);
            Log.info("PipelineManager", "Make sure your pipeline implements a public constructor with no parameters or with a Telemetry parameter");

            Log.white();

        } catch (Exception ex) {

            eocvSim.visualizer.asyncPleaseWaitDialog("Error while initializing requested pipeline", "Falling back to previous one",
                    "Close", new Dimension(300, 150), true, true);

            Log.error("PipelineManager", "Error while initializing requested pipeline (" + pipelineClass.getSimpleName() + ")", ex);
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

        //if pause on images option is turned on by user
        if (eocvSim.configManager.getConfig().pauseOnImages)
            eocvSim.inputSourceManager.pauseIfImageTwoFrames(); //pause next frame if current selected inputsource is an image

        onPipelineChange.run();

    }

    public void requestChangePipeline(int index) {
        onUpdate.doOnce(() -> changePipeline(index));
    }

    public void runThenPause() {
        setPaused(false);
        eocvSim.onMainUpdate.doOnce(() -> setPaused(true));
    }

    public void setPaused(boolean paused, PauseReason pauseReason) {

        isPaused = paused;

        if (isPaused) {
            lastPauseReason = pauseReason;
            onPause.run();
        } else {
            lastPauseReason = PauseReason.NOT_PAUSED;
            onResume.run();
        }

        eocvSim.visualizer.pipelinePauseBtt.setSelected(isPaused);

    }

    public void togglePause() {
        setPaused(!isPaused);
    }

    public void requestSetPaused(boolean paused, PauseReason pauseReason) {
        eocvSim.onMainUpdate.doOnce(() -> setPaused(paused, pauseReason));
    }

    public void requestSetPaused(boolean paused) {
        requestSetPaused(paused, PauseReason.USER_REQUESTED);
    }

    public boolean isPaused() {
        if (!isPaused) lastPauseReason = PauseReason.NOT_PAUSED;
        return isPaused;
    }

    public void setPaused(boolean paused) {
        setPaused(paused, PauseReason.USER_REQUESTED);
    }

    public PauseReason getPauseReason() {
        if (!isPaused) lastPauseReason = PauseReason.NOT_PAUSED;
        return lastPauseReason;
    }

    public enum PauseReason {USER_REQUESTED, IMAGE_ONE_ANALYSIS, NOT_PAUSED}

}
