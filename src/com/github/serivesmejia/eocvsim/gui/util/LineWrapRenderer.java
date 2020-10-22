package com.github.serivesmejia.eocvsim.gui.util;

import javax.swing.*;
import java.awt.*;

public class LineWrapRenderer extends DefaultListCellRenderer {

    public static final String HTML_1 = "<html><body style='width: ";
    public static final String HTML_2 = "px'>";
    public static final String HTML_3 = "</html>";

    private int width;

    public LineWrapRenderer(int width) {
        this.width = width;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        String text = HTML_1 + String.valueOf(width) + HTML_2 + value.toString() + HTML_3;

        return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);

    }

}