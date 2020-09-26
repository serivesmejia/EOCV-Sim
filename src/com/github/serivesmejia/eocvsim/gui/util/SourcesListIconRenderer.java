package com.github.serivesmejia.eocvsim.gui.util;

import com.github.serivesmejia.eocvsim.input.InputSourceManager;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import static com.github.serivesmejia.eocvsim.gui.util.GuiUtil.scaleImage;

public class SourcesListIconRenderer extends DefaultListCellRenderer {

    public static ImageIcon ICON_IMG = null;
    public static ImageIcon ICON_WEBCAM = null;

    public InputSourceManager sourceManager = null;

    public SourcesListIconRenderer(InputSourceManager sourceManager) throws IOException {
        ICON_IMG = scaleImage(GuiUtil.loadImageIcon("/resources/images/icon/ico_img.png"), 15, 15);
        ICON_WEBCAM = scaleImage(GuiUtil.loadImageIcon("/resources/images/icon/ico_cam.png"), 15, 15);
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