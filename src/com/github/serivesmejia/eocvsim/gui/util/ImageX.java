package com.github.serivesmejia.eocvsim.gui.util;

import com.github.serivesmejia.eocvsim.util.CvUtil;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImageX extends JLabel {

    volatile ImageIcon icon;

    public ImageX() {
        super();
    }

    public synchronized void setImage(BufferedImage img) {

        if (icon != null) {
            icon.getImage().flush(); //flush old image :p
        } else {
            icon = null;
            setIcon(icon);
            return;
        }

        icon = new ImageIcon(img);

        setIcon(icon); //set to the new image

    }

    public synchronized void setImageMat(Mat m) {
        setImage(CvUtil.matToBufferedImage(m));
    }

}
