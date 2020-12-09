package com.github.serivesmejia.eocvsim.gui.component;

import com.github.serivesmejia.eocvsim.util.CvUtil;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageX extends JLabel {

    volatile ImageIcon icon;

    public ImageX() {
        super();
    }

    public synchronized void setImage(BufferedImage img) {

        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (icon != null)
            icon.getImage().flush(); //flush old image :p

        icon = new ImageIcon(img);

        setIcon(icon); //set to the new image

    }

    public synchronized void setImageMat(Mat m) {
        setImage(CvUtil.matToBufferedImage(m));
    }

}
