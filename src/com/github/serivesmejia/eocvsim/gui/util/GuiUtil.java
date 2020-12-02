package com.github.serivesmejia.eocvsim.gui.util;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.DialogFactory;
import com.github.serivesmejia.eocvsim.gui.dialog.FileAlreadyExists;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.SysUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
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

    public static ImageIcon loadImageIcon(String path) throws IOException {
        return new ImageIcon(loadBufferedImage(path));
    }

    public static BufferedImage loadBufferedImage(String path) throws IOException {
        return ImageIO.read(GuiUtil.class.getResourceAsStream(path));
    }

    public static void saveBufferedImage(File file, BufferedImage bufferedImage, String format) throws IOException {
        ImageIO.write(bufferedImage, format, file);
    }

    public static void saveBufferedImage(File file, BufferedImage bufferedImage) throws IOException {
        saveBufferedImage(file, bufferedImage, "jpg");
    }

    public static void catchSaveBufferedImage(File file, BufferedImage bufferedImage, String format) {
        try {
            saveBufferedImage(file, bufferedImage, format);
        } catch (IOException e) {
            Log.error("GuiUtil", "Failed to save buffered image", e);
        }
    }

    public static void catchSaveBufferedImage(File file, BufferedImage bufferedImage) {
        catchSaveBufferedImage(file, bufferedImage, "jpg");
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

    public static void saveBufferedImageFileChooser(Component parent, BufferedImage bufferedImage, EOCVSim eocvSim) {

        FileNameExtensionFilter jpegFilter = new FileNameExtensionFilter("JPEG (*.jpg)",  "jpg", "jpeg");
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)",  "png");

        DialogFactory.createFileChooser(parent, DialogFactory.FileChooser.Mode.SAVE_FILE_SELECT, jpegFilter, pngFilter)

        .addCloseListener((MODE, selectedFile) -> {
            if(MODE == JFileChooser.APPROVE_OPTION) {
                Optional<String> extension = SysUtil.getExtensionByStringHandling(selectedFile.getName());

                if(!selectedFile.exists()) {
                    if(extension.isPresent()) {
                        catchSaveBufferedImage(selectedFile, bufferedImage, extension.get());
                    } else {
                        catchSaveBufferedImage(selectedFile, bufferedImage);
                    }
                } else {
                    FileAlreadyExists.UserChoice userChoice = new DialogFactory(eocvSim).fileAlreadyExists(); //create confirm dialog
                    if(userChoice == FileAlreadyExists.UserChoice.REPLACE) {
                        if(extension.isPresent()) {
                            catchSaveBufferedImage(selectedFile, bufferedImage, extension.get());
                        } else {
                            catchSaveBufferedImage(selectedFile, bufferedImage);
                        }
                    }
                }

            }
        });

    }

}
