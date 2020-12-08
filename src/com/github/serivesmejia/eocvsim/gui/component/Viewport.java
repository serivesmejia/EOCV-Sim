package com.github.serivesmejia.eocvsim.gui.component;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.config.Config;
import com.github.serivesmejia.eocvsim.gui.util.MatPoster;
import com.github.serivesmejia.eocvsim.util.Log;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;

public class Viewport extends JPanel {

    public final ImageX image = new ImageX();

    private Mat lastVisualizedMat = null;
    private Mat lastVisualizedScaledMat = null;

    private double scale;

    private final EOCVSim eocvSim;

    public Viewport(EOCVSim eocvSim) {

        super(new GridBagLayout());

        this.eocvSim = eocvSim;
        setViewportScale(eocvSim.configManager.getConfig().zoom);

        add(image, new GridBagConstraints());

    }

    public void visualizeScaleMat(Mat mat) {

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if(lastVisualizedMat == null) lastVisualizedMat = new Mat(); //create latest mat if we have null reference
        mat.copyTo(lastVisualizedMat); //copy given mat to viewport latest one

        double wScale = (double) frame.getWidth() / mat.width();
        double hScale = (double) frame.getHeight() / mat.height();

        double calcScale = (wScale / hScale) * 1.5;
        double finalScale = Math.max(0.1, Math.min(3, scale * calcScale));

        if(lastVisualizedScaledMat == null) lastVisualizedScaledMat = new Mat(); //create last scaled mat if null reference

        Size size = new Size(mat.width() * finalScale, mat.height() * finalScale);
        Imgproc.resize(mat, lastVisualizedScaledMat, size, 0.0, 0.0, Imgproc.INTER_LINEAR); //resize mat to lastVisualizedScaledMat

        image.setImageMat(lastVisualizedScaledMat); //set mat image to ImageX component

        Config config = eocvSim.configManager.getConfig();
        if (config.storeZoom) config.zoom = scale; //store latest scale if store setting turned on

    }

    public void attachToPoster(MatPoster poster) {
        poster.addPostable((m) -> {
            try {
                Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2BGR);
                this.visualizeScaleMat(m);
            } catch(Exception ex) {
                Log.error("Viewport-Postable", "Couldn't visualize last mat", ex);
            }
        });
    }

    public void setViewportScale(double scale) {
        boolean scaleChanged = this.scale != scale;
        this.scale = scale;

        if(lastVisualizedMat != null && scaleChanged)
            visualizeScaleMat(lastVisualizedMat);
    }

    public Mat getLastVisualizedMat() {
        return lastVisualizedMat;
    }

    public double getViewportScale() {
        return scale;
    }

}
