package com.github.serivesmejia.eocvsim.gui.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GuiUtil {

    public static void jTextFieldOnlyNumbers(JTextField field, int minNumber, int onMinNumberChangeTo) {

        ((AbstractDocument)field.getDocument()).setDocumentFilter(new DocumentFilter(){
            Pattern regEx = Pattern.compile("\\d*");

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if(!matcher.matches()){
                    return;
                }

                if(field.getText().length() == 0) {
                    try {
                        int number = Integer.parseInt(text);
                        if (number <= minNumber) {
                            text = String.valueOf(onMinNumberChangeTo);
                        }
                    } catch (NumberFormatException ex) {  }
                }

                super.replace(fb, offset, length, text, attrs);
            }
        });

    }


    public static ImageIcon scaleImage(ImageIcon icon, int w, int h) {

        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();

        if(icon.getIconWidth() > w) {
            nw = w;
            nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }

        if(nh > h) {
            nh = h;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }

        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));

    }

    public static BufferedImage scaleImage(BufferedImage image, double scale) {

        if(scale <= 0) scale = 1;

        int w = (int)Math.round(scale*(double)image.getWidth());
        int h = (int)Math.round(scale*(double)image.getHeight());

        if(w <= 0) w = image.getWidth();
        if(h <= 0) h = image.getHeight();

        BufferedImage bi = new BufferedImage(w, h, image.getType());
        Graphics2D g2 = bi.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        AffineTransform at = AffineTransform.getScaleInstance(scale, scale);

        g2.drawRenderedImage(image, at);
        g2.dispose();

        return bi;

    }

    public static ImageIcon loadImageIcon(String path) throws IOException {
        return new ImageIcon(loadBufferedImage(path));
    }

    public static BufferedImage loadBufferedImage(String path) throws IOException {
        return ImageIO.read(GuiUtil.class.getResourceAsStream(path));
    }

    public static void invertBufferedImageColors(BufferedImage input) {

        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {

                int rgba = input.getRGB(x, y);
                Color col = new Color(rgba, true);

                if(col.getAlpha() <= 0) continue;

                col = new Color(255 - col.getRed(),
                        255 - col.getGreen(),
                        255 - col.getBlue());

                input.setRGB(x, y, col.getRGB());

            }
        }

    }

}
