package com.github.serivesmejia.eocvsim.input;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.input.source.ImageSource;
import com.github.serivesmejia.eocvsim.pipeline.PipelineManager;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.SysUtil;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InputSourceManager {

    private final EOCVSim eocvSim;

    public volatile Mat lastMatFromSource = null;
    public volatile InputSource currentInputSource = null;

    public volatile HashMap<String, InputSource> sources = new HashMap<>();

    public InputSourceLoader inputSourceLoader = new InputSourceLoader();

    public InputSourceManager(EOCVSim eocvSim) {
        this.eocvSim = eocvSim;
    }

    public static SourceType getSourceType(InputSource source) {

        switch (source.getClass().getSimpleName()) {
            case "ImageSource":
                return SourceType.IMAGE;
            case "CameraSource":
                return SourceType.CAMERA;
            case "VideoSource":
                return SourceType.VIDEO;
        }

        return SourceType.UNKNOWN;

    }

    public void init() {

        Log.info("InputSourceManager", "Initializing...");

        inputSourceLoader.loadInputSourcesFromFile();

        for (Map.Entry<String, InputSource> entry : inputSourceLoader.loadedInputSources.entrySet()) {
            addInputSource(entry.getKey(), entry.getValue());
        }

        Size size = new Size(320, 240);
        createDefaultImgInputSource("/images/ug_4.jpg", "ug_eocvsim_4.jpg", "Ultimate Goal 4 Ring", size);
        createDefaultImgInputSource("/images/ug_1.jpg", "ug_eocvsim_1.jpg", "Ultimate Goal 1 Ring", size);
        createDefaultImgInputSource("/images/ug_0.jpg", "ug_eocvsim_0.jpg", "Ultimate Goal 0 Ring", size);

        lastMatFromSource = new Mat();

        Log.white();

    }

    private void createDefaultImgInputSource(String resourcePath, String fileName, String sourceName, Size imgSize) {
        try {

            InputStream is = InputSource.class.getResourceAsStream(resourcePath);
            File f = SysUtil.copyFileIsTemp(is, fileName, true).file;

            ImageSource src = new ImageSource(f.getAbsolutePath(), imgSize);
            src.isDefault = true;

            addInputSource(sourceName, src);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(boolean isPaused) {

        if (currentInputSource == null) return;
        currentInputSource.setPaused(isPaused);

        try {
            lastMatFromSource = currentInputSource.update();
        } catch (Throwable ex) {
            Log.error("InputSourceManager", "Error while processing current source", ex);
        }

    }

    public void addInputSource(String name, InputSource inputSource) {

        if (inputSource == null) {
            currentInputSource = null;
            return;
        }

        if (sources.containsKey(name)) return;

        inputSource.name = name;

        sources.put(name, inputSource);

        inputSourceLoader.saveInputSource(name, inputSource);
        inputSourceLoader.saveInputSourcesToFile();

        Log.info("InputSourceManager", "Adding InputSource " + inputSource.toString() + " (" + inputSource.getClass().getSimpleName() + ")");

    }

    public void deleteInputSource(String sourceName) {

        InputSource src = sources.get(sourceName);

        if (src == null) return;
        if (src.isDefault) return;

        sources.remove(sourceName);

        inputSourceLoader.deleteInputSource(sourceName);
        inputSourceLoader.saveInputSourcesToFile();

    }

    public boolean setInputSource(String sourceName) {

        InputSource src = sources.get(sourceName);

        if (src != null) {
            src.reset();
            src.eocvSim = eocvSim;
        }

        //check if source type is a camera, and if so, create a please wait dialog
        Visualizer.AsyncPleaseWaitDialog apwdCam = checkCameraDialogPleaseWait(sourceName);

        if (src != null) {
            if (!src.init()) {

                if (apwdCam != null) {
                    apwdCam.destroyDialog();
                }

                eocvSim.visualizer.asyncPleaseWaitDialog("Error while loading requested source", "Falling back to previous source",
                        "Close", new Dimension(300, 150), true, true);

                Log.error("InputSourceManager", "Error while loading requested source (" + sourceName + ") reported by itself (init method returned false)");

                return false;

            }
        }

        //if there's a please wait dialog for a camera source, destroy it.
        if (apwdCam != null) {
            apwdCam.destroyDialog();
        }

        if (currentInputSource != null) {
            currentInputSource.reset();
        }

        currentInputSource = src;

        //if pause on images option is turned on by user
        if (eocvSim.configManager.getConfig().pauseOnImages)
            pauseIfImage();

        Log.info("InputSourceManager", "Set InputSource to " + currentInputSource.toString() + " (" + src.getClass().getSimpleName() + ")");

        //enable or disable source delete button depending if source is default or not
        eocvSim.visualizer.sourceSelectorDeleteBtt.setEnabled(!currentInputSource.isDefault);

        return true;

    }

    public void pauseIfImage() {
        //if the new input source is an image, we will pause the next frame
        //to execute one shot analysis on images and save resources.
        if (getSourceType(currentInputSource) == SourceType.IMAGE) {
            eocvSim.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    eocvSim.pipelineManager.setPaused(true, PipelineManager.PauseReason.IMAGE_ONE_ANALYSIS);
                }
            });
        }
    }

    public void pauseIfImageTwoFrames() {
        //if the new input source is an image, we will pause the next frame
        //to execute one shot analysis on images and save resources.
        eocvSim.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                pauseIfImage();
            }
        });
    }

    public void requestSetInputSource(String name) {
        eocvSim.runOnMainThread(() -> setInputSource(name));
    }

    public Visualizer.AsyncPleaseWaitDialog checkCameraDialogPleaseWait(String sourceName) {

        Visualizer.AsyncPleaseWaitDialog apwdCam = null;

        if (getSourceType(sourceName) == SourceType.CAMERA) {
            apwdCam = eocvSim.visualizer.asyncPleaseWaitDialog("Opening camera...", null, "Exit",
                    new Dimension(300, 150), true);
            apwdCam.onCancel(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            });
        }

        return apwdCam;

    }

    public SourceType getSourceType(String sourceName) {

        InputSource source = sources.get(sourceName);

        return getSourceType(source);

    }

    public enum SourceType {
        IMAGE,
        CAMERA,
        VIDEO,
        UNKNOWN
    }

}
