package com.github.serivesmejia.eocvsim.gui.util;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImageX extends JLabel {

    volatile ImageIcon icon;

    public ImageX() {
        super();
    }

    public synchronized void setImage(BufferedImage img) {

        if(icon != null)
            icon.getImage().flush(); //flush old image :p

        icon = new ImageIcon(img);

        setIcon(icon); //set to the new image

    }

}
