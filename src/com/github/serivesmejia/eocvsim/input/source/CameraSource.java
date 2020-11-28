package com.github.serivesmejia.eocvsim.input.source;

import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.input.InputSource;
import com.github.serivesmejia.eocvsim.util.Log;

import com.google.gson.annotations.Expose;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.openftc.easyopencv.MatRecycler;

import java.util.Objects;

public class CameraSource extends InputSource {

    private transient VideoCapture camera = null;
    private transient Mat lastFramePaused = null;
    private transient Mat lastFrame = null;

    private transient boolean initialized = false;

    @Expose
    private final int webcamIndex;
    @Expose
    private volatile Size size;

    private MatRecycler matRecycler = new MatRecycler(2);

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

        MatRecycler.RecyclableMat newFrame = matRecycler.takeMat();

        camera.read(newFrame);

        if(newFrame.empty()) {
            Log.error("CameraSource", "Unable to open camera " + webcamIndex + ", returned Mat was empty.");
            newFrame.release();
            return false;
        }

        matRecycler.returnMat(newFrame);

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

        if(isPaused) {
            return lastFramePaused;
        } else if(lastFramePaused != null){
            lastFramePaused.release();
            lastFramePaused = null;
        }

        if(lastFrame == null) lastFrame = new Mat();
        if(camera == null) return lastFrame;

        camera.read(lastFrame);

        if(lastFrame.empty()) return lastFrame;

        if(size == null) size = lastFrame.size();

        Imgproc.cvtColor(lastFrame, lastFrame, Imgproc.COLOR_BGR2RGB);
        Imgproc.resize(lastFrame, lastFrame, size, 0.0, 0.0, Imgproc.INTER_CUBIC);

        return lastFrame;

    }

    @Override
    public void onPause() {

        if(lastFrame != null) lastFrame.release();
        if(lastFramePaused == null) lastFramePaused = new Mat();

        camera.read(lastFramePaused);

        Imgproc.cvtColor(lastFramePaused, lastFramePaused, Imgproc.COLOR_BGR2RGB);
        Imgproc.resize(lastFramePaused, lastFramePaused, size, 0.0, 0.0, Imgproc.INTER_LINEAR);

        update();

        camera.release();
        camera = null;

    }

    @Override
    public void onResume() {
        Visualizer.AsyncPleaseWaitDialog apwdCam = eocvSim.inputSourceManager.checkCameraDialogPleaseWait(name);
        camera = new VideoCapture(webcamIndex);
        camera.open(webcamIndex);
        apwdCam.destroyDialog();
    }

    @Override
    public InputSource cloneSource() {
        return new CameraSource(webcamIndex, size);
    }

    @Override
    public String toString() {
        if(size == null) size = new Size();
        return "CameraSource(" + webcamIndex + ", " + (size != null ? size.toString() : "null") + ")";
    }

}