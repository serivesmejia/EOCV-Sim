package com.github.serivesmejia.eocvsim.gui.component;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.config.Config;
import com.github.serivesmejia.eocvsim.gui.util.MatPoster;
import com.github.serivesmejia.eocvsim.util.image.BufferedImageGiver;
import com.github.serivesmejia.eocvsim.util.CvUtil;
import com.github.serivesmejia.eocvsim.util.Log;
import com.qualcomm.robotcore.util.Range;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Viewport extends JPanel {

    public final ImageX image = new ImageX();
    public final MatPoster matPoster;

    private Mat lastVisualizedMat = null;
    private Mat lastVisualizedScaledMat = null;

    private final BufferedImageGiver buffImgGiver = new BufferedImageGiver();

    private volatile BufferedImage lastBuffImage;

    private double scale;

    private final EOCVSim eocvSim;

    public Viewport(EOCVSim eocvSim, int maxQueueItems) {

        super(new GridBagLayout());

        this.eocvSim = eocvSim;
        setViewportScale(eocvSim.configManager.getConfig().zoom);

        add(image, new GridBagConstraints());

        matPoster = new MatPoster(maxQueueItems);
        attachToPoster(matPoster);

    }

    public synchronized void postMat(Mat mat) {
        matPoster.post(mat);
    }

    public synchronized void visualizeScaleMat(Mat mat) {

        if(lastBuffImage != null) buffImgGiver.returnBufferedImage(lastBuffImage);

        if(lastVisualizedMat == null) lastVisualizedMat = new Mat(); //create latest mat if we have null reference
        if(lastVisualizedScaledMat == null) lastVisualizedScaledMat = new Mat(); //create last scaled mat if null reference

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        mat.copyTo(lastVisualizedMat); //copy given mat to viewport latest one

        double wScale = (double) frame.getWidth() / mat.width();
        double hScale = (double) frame.getHeight() / mat.height();

        double calcScale = (wScale / hScale) * 1.5;
        double finalScale = Math.max(0.1, Math.min(3, scale * calcScale));

        Size size = new Size(mat.width() * finalScale, mat.height() * finalScale);
        Imgproc.resize(mat, lastVisualizedScaledMat, size, 0.0, 0.0, Imgproc.INTER_LINEAR); //resize mat to lastVisualizedScaledMat

        lastBuffImage = buffImgGiver.giveBufferedImage(new Dimension(lastVisualizedScaledMat.width(), lastVisualizedScaledMat.height()), 3);
        CvUtil.matToBufferedImage(lastVisualizedScaledMat, lastBuffImage);

        image.setImage(lastBuffImage); //set buff image to ImageX component

        Config config = eocvSim.configManager.getConfig();
        if (config.storeZoom) config.zoom = scale; //store latest scale if store setting turned on

    }

    public void attachToPoster(MatPoster poster) {
        poster.addPostable((m) -> {
            try {
                Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2BGR);
                visualizeScaleMat(m);
            } catch(Exception ex) {
                Log.error("Viewport-Postable", "Couldn't visualize last mat", ex);
            }
        });
    }

    public void flush() {
        buffImgGiver.flushAll();
    }

    public void stop() {
        matPoster.stop();
        flush();
    }

    public synchronized void setViewportScale(double scale) {

        scale = Range.clip(scale, 0.1, 3);

        boolean scaleChanged = this.scale != scale;
        this.scale = scale;

        if(lastVisualizedMat != null && scaleChanged)
            visualizeScaleMat(lastVisualizedMat);

    }

    public synchronized Mat getLastVisualizedMat() {
        return lastVisualizedMat;
    }

    public synchronized double getViewportScale() {
        return scale;
    }

}
