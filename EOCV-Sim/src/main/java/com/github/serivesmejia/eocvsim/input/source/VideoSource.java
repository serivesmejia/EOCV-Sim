package com.github.serivesmejia.eocvsim.input.source;

import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.input.InputSource;
import com.github.serivesmejia.eocvsim.util.Log;
import com.google.gson.annotations.Expose;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.openftc.easyopencv.MatRecycler;

import java.util.Objects;

public class VideoSource extends InputSource {

    @Expose
    private final String videoPath;

    private transient VideoCapture video = null;

    private transient MatRecycler.RecyclableMat lastFramePaused = null;
    private transient MatRecycler.RecyclableMat lastFrame = null;

    private transient boolean initialized = false;

    @Expose
    private volatile Size size;

    private volatile transient MatRecycler matRecycler = null;

    public VideoSource(String videoPath, Size size) {
        this.videoPath = videoPath;
        this.size = size;
    }

    @Override
    public boolean init() {

        if (initialized) return false;
        initialized = true;

        video = new VideoCapture();
        video.open(videoPath);

        if (!video.isOpened()) {
            Log.error("VideoSource", "Unable to open video " + videoPath);
            return false;
        }

        if (matRecycler == null) matRecycler = new MatRecycler(4);

        MatRecycler.RecyclableMat newFrame = matRecycler.takeMat();
        newFrame.release();

        video.read(newFrame);

        if (newFrame.empty()) {
            Log.error("VideoSource", "Unable to open video " + videoPath + ", returned Mat was empty.");
            return false;
        }

        newFrame.release();
        matRecycler.returnMat(newFrame);

        return true;

    }

    @Override
    public void reset() {

        if (!initialized) return;

        if (video != null && video.isOpened()) video.release();

        if(lastFrame != null && lastFrame.isCheckedOut())
            lastFrame.returnMat();
        if(lastFramePaused != null && lastFramePaused.isCheckedOut())
            lastFramePaused.returnMat();

        matRecycler.releaseAll();

        video = null;
        initialized = false;

    }

    @Override
    public void close() {

        if(video != null && video.isOpened()) video.release();
        if(lastFrame != null) lastFrame.returnMat();

        if (lastFramePaused != null) {
            lastFramePaused.returnMat();
            lastFramePaused = null;
        }

    }

    @Override
    public Mat update() {

        if (isPaused) {
            return lastFramePaused;
        } else if (lastFramePaused != null) {
            matRecycler.returnMat(lastFramePaused);
            lastFramePaused = null;
        }

        if (lastFrame == null) lastFrame = matRecycler.takeMat();
        if (video == null) return lastFrame;

        MatRecycler.RecyclableMat newFrame = matRecycler.takeMat();

        video.read(newFrame);

        if (newFrame.empty()) {
            newFrame.returnMat();
            video.set(Videoio.CAP_PROP_POS_FRAMES, 0);
            return lastFrame;
        }

        if (size == null) size = lastFrame.size();

        Imgproc.cvtColor(newFrame, lastFrame, Imgproc.COLOR_BGR2RGB);
        Imgproc.resize(lastFrame, lastFrame, size, 0.0, 0.0, Imgproc.INTER_AREA);

        matRecycler.returnMat(newFrame);

        Log.info(String.valueOf(video.get(Videoio.CAP_PROP_POS_FRAMES)));

        return lastFrame;

    }

    @Override
    public void onPause() {

        if (lastFrame != null) lastFrame.release();
        if (lastFramePaused == null) lastFramePaused = matRecycler.takeMat();

        video.read(lastFramePaused);

        Imgproc.cvtColor(lastFramePaused, lastFramePaused, Imgproc.COLOR_BGR2RGB);
        Imgproc.resize(lastFramePaused, lastFramePaused, size, 0.0, 0.0, Imgproc.INTER_AREA);

        update();

        video.release();
        video = null;

    }

    @Override
    public void onResume() {
        video = new VideoCapture();
        video.open(videoPath);
    }

    @Override
    public InputSource internalCloneSource() {
        return new VideoSource(videoPath, size);
    }

    @Override
    public String toString() {
        return "VideoSource(" + videoPath + ", " + (size != null ? size.toString() : "null") + ")";
    }

}