package com.github.serivesmejia.eocvsim.input;

import com.github.serivesmejia.eocvsim.util.Log;
import com.google.gson.annotations.Expose;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;

import java.util.Objects;

public class CameraSource extends InputSource {

    private VideoCapture camera = null;
    private Mat lastFrame = null;

    private boolean initialized = false;

    @Expose
    private final int webcamIndex;

    private Size lastSize = new Size();

    public CameraSource(int webcamIndex) {
        this.webcamIndex = webcamIndex;
    }

    @Override
    public void init() {

        if(initialized) return;
        initialized = true;

        camera = new VideoCapture(webcamIndex);

        if(!camera.isOpened()) {
            Log.error("CameraSource", "Unable to open camera " + webcamIndex);
        }

    }

    @Override
    public void reset() {

        if(!initialized) return;

        if(camera != null && Objects.requireNonNull(camera).isOpened()) camera.release();

        camera = null;
        initialized = false;

    }

    @Override
    public void close() {

        if(camera != null && Objects.requireNonNull(camera).isOpened()) camera.release();

    }

    @Override
    public Mat update() {

        lastFrame = new Mat();

        camera.read(lastFrame);
        lastSize = lastFrame.size();
        return lastFrame;

    }

    @Override
    public InputSource cloneSource() {
        return new CameraSource(webcamIndex);
    }

    @Override
    public String toString() {
        return "CameraSource(" + webcamIndex + ", " + lastSize.toString() + ")";
    }

}
