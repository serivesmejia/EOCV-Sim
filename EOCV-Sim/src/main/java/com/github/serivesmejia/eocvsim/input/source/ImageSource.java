package com.github.serivesmejia.eocvsim.input.source;

import com.github.serivesmejia.eocvsim.input.InputSource;
import com.google.gson.annotations.Expose;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.MatRecycler;

public class ImageSource extends InputSource {

    @Expose
    private final String imgPath;
    @Expose
    private volatile Size size;

    private volatile transient MatRecycler.RecyclableMat img;
    private volatile transient MatRecycler.RecyclableMat lastCloneTo;

    private volatile transient boolean initialized = false;

    private volatile transient MatRecycler matRecycler = new MatRecycler(2);

    public ImageSource(String imgPath) {
        this(imgPath, null);
    }

    public ImageSource(String imgPath, Size size) {
        this.imgPath = imgPath;
        this.size = size;
    }

    @Override
    public boolean init() {

        if (initialized) return false;
        initialized = true;

        if (matRecycler == null) matRecycler = new MatRecycler(2);

        readImage();

        return img != null && !img.empty();

    }

    @Override
    public void onPause() {
        //if(img != null) img.release();
        System.gc();
    }

    @Override
    public void onResume() {
    }

    @Override
    public void reset() {

        if (!initialized) return;

        if (lastCloneTo != null) {
            matRecycler.returnMat(lastCloneTo);
            lastCloneTo = null;
        }

        if (img != null) {
            matRecycler.returnMat(img);
            img = null;
        }

        matRecycler.releaseAll();

        initialized = false;

    }

    public void close() {

        if (img != null) {
            matRecycler.returnMat(img);
            img = null;
        }

        if (lastCloneTo != null) {
            matRecycler.returnMat(lastCloneTo);
            lastCloneTo = null;
        }

        matRecycler.releaseAll();

    }

    public void readImage() {

        Mat readMat = Imgcodecs.imread(this.imgPath);

        if (img == null) img = matRecycler.takeMat();

        if (readMat.empty()) {
            return;
        }

        readMat.copyTo(img);
        readMat.release();

        if (this.size != null) {
            Imgproc.resize(img, img, this.size, 0.0, 0.0, Imgproc.INTER_AREA);
        } else {
            this.size = img.size();
        }

        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2RGB);

    }

    @Override
    public Mat update() {

        if (isPaused) return lastCloneTo;

        if (lastCloneTo == null) lastCloneTo = matRecycler.takeMat();

        if (img == null) return null;

        img.copyTo(lastCloneTo);

        return lastCloneTo;

    }

    @Override
    public InputSource cloneSource() {
        return new ImageSource(imgPath, size);
    }

    @Override
    public String toString() {
        if (size == null) size = new Size();
        return "ImageSource(\"" + imgPath + "\", " + (size != null ? size.toString() : "null") + ")";
    }

}
