package com.github.serivesmejia.eocvsim.input;

import com.github.serivesmejia.eocvsim.util.Log;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraSource implements InputSource {

    private VideoCapture camera = null;
    public Mat lastFrame = null;

    private boolean initialized = false;
    private int webcamIndex = 0;

    public CameraSource(int webcamIndex) {
        this.webcamIndex = webcamIndex;
    }

    @Override
    public void init() {

        if(initialized) return;
        initialized = true;

        camera = new VideoCapture(webcamIndex);

        if(!camera.isOpened()) {
            Log.error("CameraSource", "Unable to open WebCam " + String.valueOf(webcamIndex));
        }

    }

    @Override
    public void reset() {

        if(!initialized) return;

        if(camera != null || camera.isOpened()) camera.release();

        camera = null;
        initialized = false;

    }

    @Override
    public void close() {

        if(camera != null || camera.isOpened()) camera.release();

    }

    @Override
    public Mat update() {

        lastFrame = new Mat();

        camera.read(lastFrame);
        return lastFrame;

    }

}
