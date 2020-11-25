package com.github.serivesmejia.eocvsim.gui.util;

import com.github.serivesmejia.eocvsim.input.InputSourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import static com.github.serivesmejia.eocvsim.gui.util.GuiUtil.*;

public class SourcesListIconRenderer extends DefaultListCellRenderer {

    public static ImageIcon ICON_IMG = null;
    public static ImageIcon ICON_WEBCAM = null;

    public InputSourceManager sourceManager = null;

    public SourcesListIconRenderer(InputSourceManager sourceManager, boolean isDarkTheme) throws IOException {

        if(isDarkTheme) {

            BufferedImage ICON_IMG_BI = GuiUtil.loadBufferedImage("/resources/images/icon/ico_img.png");
            BufferedImage ICON_WEBCAM_BI = GuiUtil.loadBufferedImage("/resources/images/icon/ico_cam.png");

            GuiUtil.invertBufferedImageColors(ICON_IMG_BI);
            GuiUtil.invertBufferedImageColors(ICON_WEBCAM_BI);

            ICON_IMG = scaleImage(new ImageIcon(ICON_IMG_BI), 15, 15);
            ICON_WEBCAM = scaleImage(new ImageIcon(ICON_WEBCAM_BI), 15, 15);

        } else {
            ICON_IMG = scaleImage(GuiUtil.loadImageIcon("/resources/images/icon/ico_img.png"), 15, 15);
            ICON_WEBCAM = scaleImage(GuiUtil.loadImageIcon("/resources/images/icon/ico_cam.png"), 15, 15);
        }

        this.sourceManager = sourceManager;

    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        // Get the renderer component from parent class
        JLabel label = (JLabel) super.getListCellRendererComponent(list,
                        value, index, isSelected, cellHasFocus);

        switch(sourceManager.getSourceType((String)value)) {
            case IMAGE:
                label.setIcon(ICON_IMG);
                break;
            case CAMERA:
                label.setIcon(ICON_WEBCAM);
                break;
        }

        return label;

    }

}