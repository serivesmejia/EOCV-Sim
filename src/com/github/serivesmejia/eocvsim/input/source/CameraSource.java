package com.github.serivesmejia.eocvsim.input.source;

import com.github.serivesmejia.eocvsim.input.InputSource;
import com.github.serivesmejia.eocvsim.util.Log;

import com.google.gson.annotations.Expose;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.Objects;

public class CameraSource extends InputSource {

    private VideoCapture camera = null;
    private Mat lastFrame = null;

    private boolean initialized = false;

    @Expose
    private final int webcamIndex;
    @Expose
    private volatile Size size;

    public CameraSource(int webcamIndex, Size size) {
        this.webcamIndex = webcamIndex;
        this.size = size;
    }

    @Override
    public boolean init() {

        if(initialized) return false;
        initialized = true;

        camera = new VideoCapture(webcamIndex);

        if(!camera.isOpened()) {
            Log.error("CameraSource", "Unable to open camera " + webcamIndex);
            return false;
        }

        Mat newFrame = new Mat();

        camera.read(newFrame);

        if(newFrame.empty()) {
            Log.error("CameraSource", "Unable to open camera " + webcamIndex + ", returned Mat was empty.");
            return false;
        }

        return true;

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

        if(lastFrame != null) lastFrame.release();

        lastFrame = new Mat();
        Mat newFrame = new Mat();

        camera.read(newFrame);

        if(newFrame.empty()) throw new NullPointerException();

        Imgproc.cvtColor(newFrame, newFrame, Imgproc.COLOR_BGR2RGB);
        Imgproc.resize(newFrame, lastFrame, size, 0.0, 0.0, Imgproc.INTER_LINEAR);

        newFrame.release();

        return lastFrame;

    }

    @Override
    public InputSource cloneSource() {
        return new CameraSource(webcamIndex, size);
    }

    @Override
    public String toString() {
        return "CameraSource(" + webcamIndex + ", " + size.toString() + ")";
    }

}
