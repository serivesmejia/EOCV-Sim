package com.github.serivesmejia.eocvsim.pipeline;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.Visualizer.AsyncPleaseWaitDialog;
import com.github.serivesmejia.eocvsim.util.Log;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class PipelineManager {

    private final ArrayList<Runnable> runnsOnUpdate = new ArrayList<>();
    private final ArrayList<Runnable> runnsOnChange = new ArrayList<>();
    private final ArrayList<Runnable> runnsOnPause = new ArrayList<>();
    private final ArrayList<Runnable> runnsOnResume = new ArrayList<>();

    public volatile ArrayList<Class<? extends OpenCvPipeline>> pipelines = new ArrayList<>();

    public OpenCvPipeline currentPipeline = null;
    public String currentPipelineName = "";
    public int currentPipelineIndex = -1;

    public Telemetry currentTelemetry = null;
    public volatile Mat lastOutputMat = new Mat();

    public EOCVSim eocvSim;

    private final ElapsedTime fpsElapsedTime = new ElapsedTime();

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

        //run all pending requested runnables
        for (Runnable runn : runnsOnUpdate.toArray(new Runnable[0])) {
            runn.run();
            runnsOnUpdate.remove(runn);
        }

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
        Telemetry nextTelemetry;

        Class<? extends OpenCvPipeline> pipelineClass = pipelines.get(index);

        Log.info("PipelineManager", "Changing to pipeline " + pipelineClass.getName());

        Constructor<?> constructor;
        try {

            constructor = pipelineClass.getConstructor();
            nextPipeline = (OpenCvPipeline) constructor.newInstance();

            nextTelemetry = new Telemetry();
            nextPipeline.telemetry = nextTelemetry;

            Log.info("PipelineManager", "Instantiated pipeline class " + pipelineClass.getName());

            nextPipeline.init(eocvSim.inputSourceManager.lastMatFromSource);

        } catch (Throwable ex) {

            eocvSim.visualizer.asyncPleaseWaitDialog("Error while initializing requested pipeline", "Falling back to previous one",
                    "Close", new Dimension(300, 150), true, true);

            Log.error("InputSourceManager", "Error while initializing requested pipeline (" + pipelineClass.getSimpleName() + ")", ex);
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

        for (Runnable runn : runnsOnChange.toArray(new Runnable[0])) {
            runn.run();
        }

    }

    public void requestChangePipeline(int index) {
        runOnUpdate(() -> changePipeline(index));
    }

    public void runThenPause() {

        setPaused(false);

        eocvSim.runOnMainThread(() -> setPaused(true));

    }

    public void runOnChange(Runnable runn) {
        runnsOnChange.add(runn);
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

    public void setPaused(boolean paused, PauseReason pauseReason) {

        isPaused = paused;

        if (isPaused) {
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
        eocvSim.runOnMainThread(() -> setPaused(paused, pauseReason));
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

    private void executeRunnsOnPauseOrResume() {
        if (isPaused) {
            executeRunnsOnPause();
        } else {
            executeRunnsOnResume();
        }
    }

    private void executeRunnsOnPause() {
        //run all pending requested runnables
        for (Runnable runn : runnsOnPause.toArray(new Runnable[0])) {
            runn.run();
        }
    }

    private void executeRunnsOnResume() {
        //run all pending requested runnables
        for (Runnable runn : runnsOnResume.toArray(new Runnable[0])) {
            runn.run();
        }
    }

    public enum PauseReason {USER_REQUESTED, IMAGE_ONE_ANALYSIS, NOT_PAUSED}

}
