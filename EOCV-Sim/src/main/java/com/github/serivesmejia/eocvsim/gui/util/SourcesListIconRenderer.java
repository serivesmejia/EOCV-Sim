package com.github.serivesmejia.eocvsim.gui.util;

import com.github.serivesmejia.eocvsim.input.InputSourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.github.serivesmejia.eocvsim.gui.util.GuiUtil.scaleImage;

public class SourcesListIconRenderer extends DefaultListCellRenderer {

    public static ImageIcon ICON_IMG = null;
    public static ImageIcon ICON_WEBCAM = null;
    public static ImageIcon ICON_VIDEO = null;

    private static BufferedImage ICON_IMG_BI = null;
    private static BufferedImage ICON_WEBCAM_BI = null;
    private static BufferedImage ICON_VIDEO_BI = null;

    private static boolean colorsInverted = false;

    public static final int ICO_W = 15;
    public static final int ICO_H = 15;

    public InputSourceManager sourceManager = null;

    static {
        try {
            ICON_IMG_BI = GuiUtil.loadBufferedImage("/images/icon/ico_img.png");
            ICON_WEBCAM_BI = GuiUtil.loadBufferedImage("/images/icon/ico_cam.png");
            ICON_VIDEO_BI = GuiUtil.loadBufferedImage("/images/icon/ico_vid.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void invertIconColors() {
        GuiUtil.invertBufferedImageColors(ICON_IMG_BI);
        GuiUtil.invertBufferedImageColors(ICON_WEBCAM_BI);
        GuiUtil.invertBufferedImageColors(ICON_VIDEO_BI);
    }

    public SourcesListIconRenderer(InputSourceManager sourceManager, boolean isDarkTheme) {
        if (isDarkTheme) {
            if(!colorsInverted) {
                invertIconColors();
                colorsInverted = false;
            }
        } else {
            if(colorsInverted) {
                invertIconColors();
                colorsInverted = false;
            }
        }

        ICON_IMG = scaleImage(new ImageIcon(ICON_IMG_BI), ICO_W, ICO_H);
        ICON_WEBCAM = scaleImage(new ImageIcon(ICON_WEBCAM_BI), ICO_H, ICO_W);
        ICON_VIDEO = scaleImage(new ImageIcon(ICON_VIDEO_BI), ICO_W, ICO_H);

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

        switch (sourceManager.getSourceType((String) value)) {
            case IMAGE:
                label.setIcon(ICON_IMG);
                break;
            case CAMERA:
                label.setIcon(ICON_WEBCAM);
                break;
            case VIDEO:
                label.setIcon(ICON_VIDEO);
                break;
        }

        return label;

    }

}